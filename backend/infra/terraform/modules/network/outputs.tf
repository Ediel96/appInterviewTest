output "vpc_id" {
  description = "ID de la VPC."
  value       = aws_vpc.this.id
}

output "vpc_cidr" {
  description = "CIDR de la VPC."
  value       = aws_vpc.this.cidr_block
}

output "public_subnet_ids" {
  description = "Subredes públicas para el ALB."
  value       = [for az in local.availability_zones : aws_subnet.public[az].id]
}

output "private_subnet_ids" {
  description = "Subredes privadas para las tareas ECS."
  value       = [for az in local.availability_zones : aws_subnet.private[az].id]
}

