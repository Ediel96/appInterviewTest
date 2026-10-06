variable "name" {
  description = "Prefijo para los recursos de red."
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR IPv4 de la VPC."
  type        = string
}

variable "availability_zone_count" {
  description = "Cantidad de zonas de disponibilidad a utilizar."
  type        = number
  default     = 2

  validation {
    condition     = var.availability_zone_count >= 2
    error_message = "Se requieren al menos dos zonas de disponibilidad."
  }
}

variable "single_nat_gateway" {
  description = "Usar un solo NAT Gateway para reducir costo; no es altamente disponible."
  type        = bool
  default     = false
}

variable "tags" {
  description = "Etiquetas comunes."
  type        = map(string)
  default     = {}
}

