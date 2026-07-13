terraform {
  required_version = ">= 1.0.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}



provider "aws" {
  access_key                  = "mock_access_key"
  secret_key                  = "mock_secret_key"
  region                      = "us-east-1"
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    rds = "http://localhost:4566"
    eks = "http://localhost:4566"
    iam = "http://localhost:4566"
    ec2 = "http://localhost:4566"
  }
}

# --- VPC Mock Config for Floci ---
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "main-vpc"
  }
}

resource "aws_subnet" "subnet_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.1.0/24"
  availability_zone = "us-east-1a"
}

resource "aws_subnet" "subnet_b" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.2.0/24"
  availability_zone = "us-east-1b"
}

# --- IAM Role for EKS Mock ---
resource "aws_iam_role" "eks_role" {
  name = "eks-cluster-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "eks.amazonaws.com"
        }
      }
    ]
  })
}

# --- EKS Cluster ---
resource "aws_eks_cluster" "catalog_eks" {
  name     = "eks-catalog-cluster"
  role_arn = aws_iam_role.eks_role.arn

  vpc_config {
    subnet_ids = [
      aws_subnet.subnet_a.id,
      aws_subnet.subnet_b.id
    ]
  }

  tags = {
    Environment = "Local-Floci"
  }
}



# --- Outputs ---
output "eks_cluster_name" {
  description = "Nome do Cluster EKS"
  value       = aws_eks_cluster.catalog_eks.name
}

output "eks_cluster_endpoint" {
  description = "Endpoint do Cluster EKS"
  value       = aws_eks_cluster.catalog_eks.endpoint
}


