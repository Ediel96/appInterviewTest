variable "aws_region" {
  description = "Región del bucket de estado."
  type        = string
  default     = "us-east-1"
}

variable "aws_profile" {
  description = "Perfil AWS opcional."
  type        = string
  default     = null
  nullable    = true
}

variable "aws_account_id" {
  description = "Cuenta permitida para evitar crear el bucket en la cuenta equivocada."
  type        = string
  default     = null
  nullable    = true
}

variable "project_name" {
  description = "Nombre corto y en minúsculas del proyecto."
  type        = string
  default     = "ministore"

  validation {
    condition     = can(regex("^[a-z0-9-]+$", var.project_name))
    error_message = "project_name sólo puede contener minúsculas, números y guiones."
  }
}

variable "environment" {
  description = "Ambiente cuyo estado se almacenará."
  type        = string

  validation {
    condition     = contains(["staging", "prod"], var.environment)
    error_message = "environment debe ser staging o prod."
  }
}

