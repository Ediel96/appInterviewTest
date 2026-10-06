# Infraestructura AWS de MiniStore

Terraform despliega el backend Docker de MiniStore en AWS ECS Fargate. Los
ambientes `staging` y `prod` tienen raíces, estado, VPC, subredes, cluster ECS,
servicio, ALB, grupos de seguridad, logs y repositorio ECR independientes.
Todo este árbol vive en `backend/infra/terraform` dentro del repositorio.

No se guardan credenciales ni secretos en Terraform. El backend actual sólo
consume la API pública DummyJSON, por lo que las variables de tarea no contienen
datos sensibles. Si se agregan secretos, deben inyectarse desde Secrets Manager
o SSM Parameter Store y ampliar de forma puntual el rol de ejecución.

## Arquitectura

```text
Internet
   |
   v
ALB público (2 AZ, HTTP o HTTPS/ACM)
   |
   v
ECS Fargate (subredes privadas, puerto 8080)
   |
   +--> NAT Gateway --> DummyJSON (HTTPS)
   +--> ECR / CloudWatch Logs (HTTPS)
```

- El ALB es el único origen permitido hacia el puerto `8080` de las tareas.
- Las tareas no tienen IP pública. La salida se limita a HTTPS y DNS de la VPC.
- `/actuator/health` alimenta el health check del target group.
- ECR usa tags inmutables, escaneo al publicar y política de retención.
- El rol de ejecución sólo puede autenticarse en ECR, descargar de su repositorio
  y escribir en su log group. El rol de la aplicación no tiene permisos AWS.
- ECS tiene rollback automático de despliegue y auto scaling por CPU.
- `staging` usa una tarea, Fargate Spot, un NAT y 14 días de logs para reducir
  costo. `prod` usa dos tareas, NAT por AZ, Fargate estándar, HTTPS obligatorio,
  90 días de logs y protección de eliminación del ALB.

## Estructura

```text
terraform/
├── bootstrap/                 # bucket S3 de estado, uno por cuenta/ambiente
├── environments/
│   ├── staging/               # raíz y configuración de preproducción
│   └── prod/                  # raíz y configuración de producción
└── modules/
    ├── environment/           # composición reutilizable
    ├── network/               # VPC, subredes, IGW, NAT y rutas
    └── service/               # ECR, ECS, ALB, IAM, logs y auto scaling
```

## Supuestos

- Terraform `>= 1.10`, Docker, AWS CLI y credenciales AWS ya están disponibles.
- La imagen se construye para `linux/amd64`, como define la task definition.
- Staging y producción pueden vivir en cuentas AWS distintas (recomendado). El
  `aws_account_id` impide que un comando apunte accidentalmente a otra cuenta.
- Hay al menos dos zonas de disponibilidad en la región seleccionada.
- El dominio Route 53 no está dentro del alcance. Si se usa dominio propio, el
  certificado ACM debe pertenecer a la misma región del ALB y el DNS debe apuntar
  al output `alb_dns_name`.
- Sin `certificate_arn` staging sirve HTTP. La raíz de producción exige un
  certificado ACM y una `openapi_server_url` HTTPS con dominio propio.
- NAT Gateway, ALB, Fargate y CloudWatch generan costo incluso con poco tráfico.

## 1. Crear el estado remoto una vez

El bootstrap crea un bucket privado, cifrado, versionado y protegido contra
destrucción. Ejecútelo por separado en la cuenta de cada ambiente. Su estado
inicial es local y sensible: consérvelo cifrado hasta verificar el bucket.

```bash
cd backend/infra/terraform/bootstrap
cp terraform.tfvars.example terraform.tfvars
# editar account, profile y environment
terraform init
terraform workspace new staging
terraform plan -out bootstrap.tfplan
# revisar; sólo entonces: terraform apply bootstrap.tfplan
terraform output -raw state_bucket_name
```

No se incluye ningún `apply` automático. Para el segundo ambiente cambie perfil,
cuenta y `environment`, y cree el workspace correspondiente (`terraform
workspace new prod`). Una precondición impide usar un workspace cuyo nombre no
coincida con `environment`, evitando mezclar los estados locales de bootstrap.

Cada ambiente usa un bucket distinto y lock nativo de S3 (`use_lockfile`), por
lo que ni el estado ni el bloqueo se comparten. Copie el ejemplo sin versionarlo:

```bash
cd ../environments/staging
cp backend.hcl.example backend.hcl
cp terraform.tfvars.example terraform.tfvars
# reemplazar IDs, perfiles y nombres de bucket
terraform init -backend-config=backend.hcl
```

Para producción se repite desde `environments/prod`. En automatización, use
roles OIDC/STS en lugar de claves estáticas o perfiles en los archivos.

## 2. Validar y revisar

Desde la raíz del ambiente:

```bash
terraform fmt -check -recursive ../../
terraform validate
terraform plan -var-file=terraform.tfvars -out deployment.tfplan
terraform show deployment.tfplan
```

Los archivos `backend.hcl`, `terraform.tfvars`, planes y estados están ignorados.
Los `*.example` no tienen secretos y sí deben versionarse.

## 3. Publicar la primera imagen

ECR debe existir antes del primer push. En el primer despliegue, cree únicamente
el repositorio declarado, publique la imagen y luego genere un plan completo:

```bash
terraform apply \
  -target='module.platform.module.service.aws_ecr_repository.this'

ECR_URL="$(aws ecr describe-repositories \
  --repository-names ministore/staging \
  --query 'repositories[0].repositoryUri' --output text)"

aws ecr get-login-password --region us-east-1 \
  | docker login --username AWS --password-stdin "${ECR_URL%/*}"

docker build --platform linux/amd64 \
  -t "$ECR_URL:2026-10-05.1" ../../../..
docker push "$ECR_URL:2026-10-05.1"

terraform plan -var-file=terraform.tfvars -out deployment.tfplan
# revisar; sólo entonces: terraform apply deployment.tfplan
```

Ajuste el repositorio (`ministore/prod`), región y tag al ambiente. El tag debe
coincidir con `image_tag` y no puede ser `latest`. Los applies dirigidos sólo se
usan para este bootstrap de ECR; los cambios normales siempre se planean como un
conjunto completo.

## 4. Verificación posterior a un despliegue autorizado

```bash
terraform output application_url
curl "$(terraform output -raw application_url)/actuator/health"
curl "$(terraform output -raw application_url)/api/products"
```

Logs:

```bash
aws logs tail "$(terraform output -raw cloudwatch_log_group)" --follow
```

Si producción tiene protección de eliminación, un `destroy` fallará de forma
intencional. Para retirarla se debe cambiar explícitamente
`enable_deletion_protection`, revisar y aplicar ese cambio antes de destruir.

## Variables por ambiente

Los valores base están en cada `main.tf`; los valores propios de cuenta están en
`terraform.tfvars.example`. No reutilice CIDRs si las VPC se conectarán en el
futuro. Para máxima separación use cuentas, perfiles/roles y buckets distintos.

Los nombres de buckets S3 son globales. El bootstrap agrega el account ID al
nombre; copie exactamente su output a `backend.hcl`.
