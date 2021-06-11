variable "aws_region" {
  default = "us-east-1"
}

variable "profile" {
  default = "default"
}

variable "access_ip" {}

variable "profile_account" {}

variable "public_key_path" {
  type      = string
  sensitive = true
}

variable "private_key_path" {
  type      = string
  sensitive = true
}

variable "certificate_arn" {
  type      = string
  sensitive = true
}

variable "shared_credentials_file" {
  type      = string
  sensitive = true
}