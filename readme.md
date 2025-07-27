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