variable "aws_region" {
  description = "Región AWS."
  type        = string
  default     = "us-east-1"
}

variable "aws_profile" {
  description = "Perfil AWS local opcional; en CI usar credenciales por rol."
  type        = string
  default     = null
  nullable    = true
}

variable "aws_account_id" {
  description = "Cuenta permitida opcional para evitar despliegues cruzados."
  type        = string
  default     = null
  nullable    = true
}

variable "project_name" {
  description = "Nombre corto del proyecto."
  type        = string
  default     = "ministore"
}

variable "vpc_cidr" {
  description = "CIDR exclusivo de producción."
  type        = string
  default     = "10.30.0.0/16"
}

variable "image_tag" {
  description = "Tag inmutable de la imagen en ECR."
  type        = string
}

variable "certificate_arn" {
  description = "Certificado ACM obligatorio para HTTPS en producción."
  type        = string

  validation {
    condition     = startswith(var.certificate_arn, "arn:aws:acm:")
    error_message = "certificate_arn debe ser un ARN de AWS Certificate Manager."
  }
}

variable "openapi_server_url" {
  description = "URL HTTPS pública obligatoria del dominio de producción."
  type        = string

  validation {
    condition     = startswith(var.openapi_server_url, "https://")
    error_message = "openapi_server_url debe comenzar con https://."
  }
}

variable "tags" {
  description = "Etiquetas adicionales."
  type        = map(string)
  default     = {}
}
