output "state_bucket_name" {
  description = "Bucket para backend.hcl del ambiente."
  value       = aws_s3_bucket.state.id
}

