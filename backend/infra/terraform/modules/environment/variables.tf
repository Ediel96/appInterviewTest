variable "project_name" {
  description = "Nombre corto del proyecto."
  type        = string
  default     = "ministore"
}

variable "aws_region" {
  description = "Región AWS donde se despliega el ambiente."
  type        = string
}

variable "environment" {
  description = "Ambiente aislado."
  type        = string

  validation {
    condition     = contains(["staging", "prod"], var.environment)
    error_message = "environment debe ser staging o prod."
  }
}

variable "vpc_cidr" {
  description = "CIDR exclusivo del ambiente."
  type        = string
}

variable "single_nat_gateway" {
  description = "Usar un NAT único para reducir costos."
  type        = bool
  default     = false
}

variable "image_tag" {
  description = "Tag inmutable de la imagen Docker en el ECR del ambiente."
  type        = string

  validation {
    condition     = length(trimspace(var.image_tag)) > 0 && var.image_tag != "latest"
    error_message = "image_tag debe ser no vacío e inmutable; no use latest."
  }
}

variable "ecr_force_delete" {
  description = "Permitir eliminar ECR aunque contenga imágenes."
  type        = bool
  default     = false
}

variable "task_cpu" {
  description = "CPU Fargate."
  type        = number
  default     = 512
}

variable "task_memory" {
  description = "Memoria Fargate en MiB."
  type        = number
  default     = 1024
}

variable "desired_count" {
  description = "Cantidad deseada inicial."
  type        = number
  default     = 1
}

variable "min_capacity" {
  description = "Mínimo de tareas."
  type        = number
  default     = 1
}

variable "max_capacity" {
  description = "Máximo de tareas."
  type        = number
  default     = 2
}

variable "use_fargate_spot" {
  description = "Ejecutar en Fargate Spot."
  type        = bool
  default     = false
}

variable "dummyjson_api_base_url" {
  description = "Endpoint externo del catálogo."
  type        = string
  default     = "https://dummyjson.com"
}

variable "dummyjson_api_timeout_ms" {
  description = "Timeout del catálogo externo en milisegundos."
  type        = number
  default     = 10000
}

variable "openapi_server_url" {
  description = "URL pública que muestra OpenAPI."
  type        = string
  default     = null
  nullable    = true
}

variable "certificate_arn" {
  description = "Certificado ACM opcional para HTTPS."
  type        = string
  default     = null
  nullable    = true
}

variable "log_retention_days" {
  description = "Días de retención de logs."
  type        = number
  default     = 30
}

variable "enable_deletion_protection" {
  description = "Activar protección de eliminación del ALB."
  type        = bool
  default     = false
}

variable "tags" {
  description = "Etiquetas adicionales."
  type        = map(string)
  default     = {}
}
