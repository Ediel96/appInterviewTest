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
  description = "CIDR exclusivo de staging."
  type        = string
  default     = "10.20.0.0/16"
}

variable "single_nat_gateway" {
  description = "Un NAT reduce costo de staging a cambio de disponibilidad."
  type        = bool
  default     = true
}

variable "image_tag" {
  description = "Tag inmutable de la imagen en ECR."
  type        = string
}

variable "certificate_arn" {
  description = "Certificado ACM opcional."
  type        = string
  default     = null
  nullable    = true
}

variable "openapi_server_url" {
  description = "URL pública opcional, normalmente el dominio DNS propio."
  type        = string
  default     = null
  nullable    = true
}

variable "tags" {
  description = "Etiquetas adicionales."
  type        = map(string)
  default     = {}
}

