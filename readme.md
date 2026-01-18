# E-Commerce Application

This is a microservices-based e-commerce application consisting of three services:

*   **Eureka Server:** Service registry and discovery.
*   **Operations Service:** Handles business operations such as checkout and payment.
*   **Product Service:** Manages product information.

## CI/CD Pipeline

This project uses a GitHub Actions workflow to automate the build, test, and deployment of the services to a Kubernetes cluster.

### Workflow

The CI/CD workflow is defined in the `.github/workflows/ci-cd.yml` file. It consists of the following jobs:

1.  **`build_and_test`:** This job is triggered on every push to the `main`, `master`, `develop`, or `publish` branches. It builds the Java applications and runs the tests for each service.
2.  **`build_and_push_docker_image`:** If the `build_and_test` job is successful, this job builds a Docker image for each service and pushes it to Docker Hub.
3.  **`deploy_to_kubernetes`:** If the `build_and_push_docker_image` job is successful, this job deploys the services to a Kubernetes cluster.

### Prerequisites

To use the CI/CD pipeline, you need to have the following secrets configured in your GitHub repository:

*   `DOCKERHUB_USERNAME`: Your Docker Hub username.
*   `DOCKERHUB_TOKEN`: Your Docker Hub access token.
*   `KUBECONFIG`: Your Kubernetes configuration file.

### Deployment

The services are deployed to a Kubernetes cluster using the manifest files in the `k8s/` directory. The following deployments and services are created:

*   `eureka-server-deployment.yml`: Deployment and service for the Eureka Server.
*   `operations-service-deployment.yml`: Deployment and service for the Operations Service.
*   `product-service-deployment.yml`: Deployment and service for the Product Service.

# Kubernetes Cluster information
EKS Cluster "akmal-test" Complete Configuration
## 📋 Cluster Basic Information
Name: akmal-test
Region: ap-southeast-3 (Asia Pacific - Jakarta)
Kubernetes Version: 1.33
Platform Version: eks.9
Status: ACTIVE
Created: August 10, 2025
ARN: arn:aws:eks:ap-southeast-3:590157534951:cluster/akmal-test
## 🌐 Networking Configuration
VPC ID: vpc-0453e9755d5c73778
Subnets:
subnet-09f63af700e6009f8
subnet-05ae7c8168588fa49
subnet-0e11068e4526a7e71
Cluster Security Group: sg-097084ef0540ff555
Service IP CIDR: 10.100.0.0/16
IP Family: IPv4
API Endpoint: https://59440F7624DD3DAEFCBE251CDFD95E91.sk1.ap-southeast-3.eks.amazonaws.com 
## 🔐 Access Configuration
Public Access: ✅ Enabled (0.0.0.0/0)
Private Access: ✅ Enabled
Authentication Mode: API
OIDC Provider: https://oidc.eks.ap-southeast-3.amazonaws.com/id/59440F7624DD3DAEFCBE251CDFD95E91 
## 👤 IAM Roles
Cluster Role: arn:aws:iam::590157534951:role/parent-role
Node Role: arn:aws:iam::590157534951:role/parent-role-node-iam
## 🖥️ Node Groups
Node Group: "larger-nodes"
Status: ACTIVE
Instance Type: t3.small
AMI Type: AL2023_x86_64_STANDARD
Disk Size: 20 GB
Capacity Type: ON_DEMAND
Kubernetes Version: 1.33
Scaling Configuration:
Min Size: 1
Max Size: 2
Desired Size: 1
Update Config: Max Unavailable = 1
Auto Scaling Group: eks-larger-nodes-b8cc4f53-74e7-2c2e-bc14-050284703676
## 📝 Logging Configuration
All logging disabled: api, audit, authenticator, controllerManager, scheduler
## 🚀 Recreation Commands
Method 1: Using eksctl (Recommended)
Create file akmal-test-cluster.yaml:

apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig

metadata:
  name: akmal-test
  region: ap-southeast-3
  version: "1.33"

vpc:
  id: vpc-0453e9755d5c73778
  subnets:
    private:
      ap-southeast-3a: { id: subnet-09f63af700e6009f8 }
      ap-southeast-3b: { id: subnet-05ae7c8168588fa49 }
      ap-southeast-3c: { id: subnet-0e11068e4526a7e71 }

iam:
  withOIDC: true
  serviceRoleARN: arn:aws:iam::590157534951:role/parent-role

managedNodeGroups:
  - name: larger-nodes
    instanceType: t3.small
    minSize: 1
    maxSize: 2
    desiredCapacity: 1
    volumeSize: 20
    volumeType: gp3
    amiFamily: AmazonLinux2023
    iam:
      instanceRoleARN: arn:aws:iam::590157534951:role/parent-role-node-iam
    tags:
      Environment: test
      Project: akmal-test

cloudWatch:
  clusterLogging:
    enable: []
    logRetentionInDays: 30

Create cluster:

eksctl create cluster -f akmal-test-cluster.yaml

Method 2: Using AWS CLI
Step 1: Create Cluster
aws eks create-cluster \
  --name akmal-test \
  --version 1.33 \
  --role-arn arn:aws:iam::590157534951:role/parent-role \
  --resources-vpc-config subnetIds=subnet-09f63af700e6009f8,subnet-05ae7c8168588fa49,subnet-0e11068e4526a7e71,endpointConfigPrivate=true,endpointConfigPublic=true,publicAccessCidrs=0.0.0.0/0 \
  --kubernetes-network-config serviceIpv4Cidr=10.100.0.0/16 \
  --region ap-southeast-3

Step 2: Wait for Cluster to be Active
aws eks wait cluster-active --name akmal-test --region ap-southeast-3

Step 3: Create Node Group
aws eks create-nodegroup \
  --cluster-name akmal-test \
  --nodegroup-name larger-nodes \
  --region ap-southeast-3 \
  --instance-types t3.small \
  --ami-type AL2023_x86_64_STANDARD \
  --node-role arn:aws:iam::590157534951:role/parent-role-node-iam \
  --subnets subnet-09f63af700e6009f8 subnet-05ae7c8168588fa49 subnet-0e11068e4526a7e71 \
  --scaling-config minSize=1,maxSize=2,desiredSize=1 \
  --disk-size 20 \
  --capacity-type ON_DEMAND

Method 3: Using Terraform
Create main.tf:

resource "aws_eks_cluster" "akmal_test" {
  name     = "akmal-test"
  role_arn = "arn:aws:iam::590157534951:role/parent-role"
  version  = "1.33"

  vpc_config {
    subnet_ids              = ["subnet-09f63af700e6009f8", "subnet-05ae7c8168588fa49", "subnet-0e11068e4526a7e71"]
    endpoint_private_access = true
    endpoint_public_access  = true
    public_access_cidrs     = ["0.0.0.0/0"]
  }

  kubernetes_network_config {
    service_ipv4_cidr = "10.100.0.0/16"
  }
}

resource "aws_eks_node_group" "larger_nodes" {
  cluster_name    = aws_eks_cluster.akmal_test.name
  node_group_name = "larger-nodes"
  node_role_arn   = "arn:aws:iam::590157534951:role/parent-role-node-iam"
  subnet_ids      = ["subnet-09f63af700e6009f8", "subnet-05ae7c8168588fa49", "subnet-0e11068e4526a7e71"]
  
  instance_types = ["t3.small"]
  ami_type       = "AL2023_x86_64_STANDARD"
  capacity_type  = "ON_DEMAND"
  disk_size      = 20

  scaling_config {
    desired_size = 1
    max_size     = 2
    min_size     = 1
  }
}

🔧 Post-Creation Steps
1. Configure kubectl
aws eks update-kubeconfig --region ap-southeast-3 --name akmal-test

2. Verify Cluster
kubectl get nodes
kubectl get pods --all-namespaces

3. Required IAM Policies for Node Role
Ensure parent-role-node-iam has these policies:

AmazonEKSWorkerNodePolicy
AmazonEKS_CNI_Policy
AmazonEC2ContainerRegistryReadOnly
AmazonEKSWorkerNodeMinimalPolicy
💾 Save This Configuration
Save this information in a file called akmal-test-cluster-backup.md so you can easily recreate your cluster anytime!

Recommendation: Use Method 1 (eksctl) as it's the easiest and handles most configurations automatically.

Sources
EKS node failed to register to cluster | AWS re:Post 

Create an Amazon EKS cluster - Amazon EKS 

EKS node creation failed | AWS re:Post 