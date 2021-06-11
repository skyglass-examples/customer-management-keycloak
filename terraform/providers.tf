terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "~> 3.0"      
    }
  }
}

provider "aws" {
    region =                  "${var.aws_region}"
    shared_credentials_file = "${var.shared_credentials_file}"
    profile                 = "${var.profile_account}"   
}