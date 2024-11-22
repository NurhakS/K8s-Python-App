
# Deploying a Dockerized Application to AWS using Jenkins, Ansible, and Terraform  

This project demonstrates the deployment of a Dockerized web application to AWS using a CI/CD pipeline. The solution integrates **Jenkins**, **Ansible**, and **Terraform** to automate the entire deployment process, showcasing best practices in cloud infrastructure provisioning, configuration management, and container orchestration.  

## **Project Workflow**  

### **1. Infrastructure Provisioning with Terraform**  
- **Terraform** provisions the AWS resources, including:  
  - VPC, Subnets, and Route Tables.  
  - EC2 instances to host the Jenkins server and the Docker application.  
  - Security Groups for secure communication.  

### **2. Configuration Management with Ansible**  
- **Ansible** configures the EC2 instances by:  
  - Installing required dependencies like Docker and setting up the application environment.  
  - Deploying the application container.  

### **3. CI/CD with Jenkins**  
- **Jenkins** is used to automate the CI/CD pipeline, including:  
  - Pulling the application code from a GitHub repository.  
  - Building a Docker image for the application.  
  - Pushing the Docker image to DockerHub.  
  - Triggering Ansible playbooks to deploy the application to AWS.  

## **Technologies Used**  
- **AWS**: EC2, VPC, Security Groups, and Route53 for cloud infrastructure.  
- **Terraform**: Infrastructure-as-Code (IaC) to provision AWS resources.  
- **Ansible**: Configuration management and application deployment.  
- **Jenkins**: Continuous Integration and Continuous Deployment.  
- **Docker**: Containerization of the application.  
- **GitHub**: Version control and collaboration.  

## **Architecture Diagram**  
*(Add your architecture diagram here if available)*  

## **Getting Started**  

### **Prerequisites**  
1. AWS Account with IAM credentials.  
2. Installed tools: Terraform, Ansible, Docker, and Jenkins.  
3. Configured GitHub repository with application code.  

### **Setup and Deployment**  

1. **Clone the Repository**  
   ```bash
   git clone <repository-url>
   cd <repository-folder>
