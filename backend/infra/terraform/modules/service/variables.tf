variable "name" {
  description = "Nombre único del servicio y ambiente."
  type        = string
}

variable "aws_region" {
  description = "Región AWS usada por el driver de logs."
  type        = string
}

variable "environment" {
  description = "Nombre lógico del ambiente."
  type        = string
}

variable "vpc_id" {
  description = "VPC donde se despliega el servicio."
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR de la VPC, usado para limitar DNS y tráfico interno."
  type        = string
}

variable "public_subnet_ids" {
  description = "Subredes públicas del balanceador."
  type        = list(string)
}

variable "private_subnet_ids" {
  description = "Subredes privadas de ECS."
  type        = list(string)
}

variable "container_port" {
  description = "Puerto HTTP de la aplicación."
  type        = number
  default     = 8080
}

variable "image_tag" {
  description = "Etiqueta inmutable o digest de la imagen publicada en ECR."
  type        = string
}

variable "ecr_force_delete" {
  description = "Permitir borrar el repositorio ECR con imágenes. Recomendado sólo en staging."
  type        = bool
  default     = false
}

variable "task_cpu" {
  description = "CPU de la tarea Fargate."
  type        = number
  default     = 512
}

variable "task_memory" {
  description = "Memoria MiB de la tarea Fargate."
  type        = number
  default     = 1024
}

variable "desired_count" {
  description = "Número inicial de tareas."
  type        = number
  default     = 1
}

variable "min_capacity" {
  description = "Mínimo de tareas para auto scaling."
  type        = number
  default     = 1
}

variable "max_capacity" {
  description = "Máximo de tareas para auto scaling."
  type        = number
  default     = 2
}

variable "autoscaling_cpu_target" {
  description = "Porcentaje de CPU objetivo."
  type        = number
  default     = 60
}

variable "use_fargate_spot" {
  description = "Usar Fargate Spot; adecuado para staging tolerante a interrupciones."
  type        = bool
  default     = false
}

variable "dummyjson_api_base_url" {
  description = "URL base del catálogo externo."
  type        = string
  default     = "https://dummyjson.com"
}

variable "dummyjson_api_timeout_ms" {
  description = "Timeout de DummyJSON en milisegundos."
  type        = number
  default     = 10000
}

variable "openapi_server_url" {
  description = "URL pública anunciada por OpenAPI; null usa el DNS del ALB."
  type        = string
  default     = null
  nullable    = true
}

variable "certificate_arn" {
  description = "ARN de certificado ACM. Si se define, habilita HTTPS y redirige HTTP."
  type        = string
  default     = null
  nullable    = true
}

variable "health_check_path" {
  description = "Ruta del health check del balanceador."
  type        = string
  default     = "/actuator/health"
}

variable "log_retention_days" {
  description = "Retención de logs en CloudWatch."
  type        = number
  default     = 30
}

variable "enable_deletion_protection" {
  description = "Protección contra eliminación accidental del ALB."
  type        = bool
  default     = false
}

variable "tags" {
  description = "Etiquetas comunes."
  type        = map(string)
  default     = {}
}
