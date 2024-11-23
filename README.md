
# Automated Docker Application Deployment to AWS with Jenkins, Ansible, Terraform, and Kubernetes

This project demonstrates an end-to-end DevOps pipeline that automates the deployment of a Dockerized application to AWS. Additionally, it highlights advanced Kubernetes knowledge by showcasing the creation of Kubernetes clusters on two VMs using shell scripts. 

The solution integrates tools and technologies such as Jenkins, Ansible, Terraform, and Kubernetes, designed to meet modern DevOps and cloud deployment standards. 

---

## Features

- **Infrastructure as Code (IaC):**  
  - Provisioned AWS infrastructure using Terraform, ensuring consistent and repeatable environments.
  
- **Configuration Management:**  
  - Configured application environments and EC2 instances using Ansible for automated setup.
  
- **Continuous Integration/Continuous Deployment (CI/CD):**  
  - Jenkins pipelines for automating builds, tests, and deployments.

- **Containerization:**  
  - Dockerized application deployed to AWS infrastructure.
  
- **Advanced Kubernetes Setup:**  
  - Created Kubernetes clusters using a custom shell script on two virtual machines.
  - Demonstrated knowledge of managing Kubernetes components like nodes, pods, and deployments.

- **Cloud Deployment:**  
  - Integrated AWS services like EC2, S3, and IAM for secure and scalable deployment.

---

## Tools and Technologies Used

- **Terraform** - Provisioning and managing cloud infrastructure.  
- **Ansible** - Automating configuration management and application deployment.  
- **Jenkins** - CI/CD pipeline orchestration.  
- **Docker** - Containerization of the application.  
- **Kubernetes** - Orchestrating containerized applications across clusters.  
- **AWS** - Cloud platform for hosting resources and services.  
- **Shell Scripting** - Automating repetitive tasks, including Kubernetes cluster creation.  

---

## Architecture Overview

1. **Jenkins Pipeline:**  
   - Source code pulled from GitHub.  
   - Builds Docker images and pushes them to Docker Hub or Amazon ECR.  
   - Uses Terraform to provision AWS infrastructure.  
   - Deploys applications via Ansible or Kubernetes.

2. **Kubernetes Cluster:**  
   - Two VMs provisioned for Kubernetes clusters.  
   - Shell script initializes the control plane and worker nodes.  
   - Deployments configured to run Dockerized applications.

3. **AWS Infrastructure:**  
   - EC2 instances for hosting Jenkins and Kubernetes nodes.  
   - S3 bucket for storing Terraform states.  
   - IAM roles for secure access.

---

## Prerequisites

- Terraform, Ansible, Docker, and Kubernetes installed locally.  
- Jenkins installed on the primary VM or localhost.  
- AWS account with required permissions.  
- Virtual machines with Linux OS for Kubernetes setup.  

---

## Setup and Usage

### Step 1: Clone the Repository
```bash
git clone https://github.com/your-username/your-repo.git
cd your-repo
```

### Step 2: Provision Infrastructure with Terraform
```bash
cd terraform
terraform init
terraform apply
```

### Step 3: Configure Instances with Ansible
```bash
cd ansible
ansible-playbook -i inventory setup.yml
```

### Step 4: Build and Push Docker Images
```bash
cd docker
docker build -t your-image .
docker push your-image
```

### Step 5: Set Up Kubernetes Cluster
Run the shell script to initialize the Kubernetes cluster:
```bash
cd k8s-setup
bash create-cluster.sh
```

### Step 6: Deploy Application via Kubernetes
Apply the Kubernetes manifest files to deploy the application:
```bash
kubectl apply -f deployment.yml
```

---

## Shell Script: Kubernetes Cluster Setup

The `create-cluster.sh` script performs the following:  
1. Installs Kubernetes dependencies on both VMs.  
2. Configures one VM as the control plane and the other as a worker node.  
3. Joins the worker node to the cluster.  
4. Sets up basic namespaces and deployment configuration.

---

## Future Enhancements

- Integration with monitoring tools like Prometheus and Grafana.  
- Scaling the Kubernetes cluster dynamically using AWS EKS.  
- Enhancing CI/CD pipelines with additional security checks.

---

## Showcase

This project demonstrates a combination of DevOps and cloud engineering expertise. The inclusion of Kubernetes adds an advanced dimension, showcasing proficiency in container orchestration and cluster management.

Feel free to explore the repository and provide feedback!

![Screenshot 2024-11-23 134632](https://github.com/user-attachments/assets/135248dc-c48b-404b-8773-88d35cd0070d)
![Screenshot 2024-11-23 171025](https://github.com/user-attachments/assets/b18e1f80-dd03-497a-abcc-b4ca5c860e75)

---

 Works With Jenkins Pipeline First One Triggers Second after Success 
 pipeline {
    agent any

    environment {
        // Set environment variables if needed
        TF_VAR_aws_access_key = 'AKIAVRUVPY6ULII53M3A' // AWS access key
        TF_VAR_aws_secret_key = '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'      // AWS secret key
    }

    stages {
        stage('Terraform Init') {
            steps {
                dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S') {
                    sh 'terraform init'
                }
                
            }
        }
        stage('Terraform Plan') {
            steps {
                dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S') {
                    sh ''' terraform plan \
                       -var="access_key=AKIAVRUVPY6ULII53M3A" \
                       -var="secret_key=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"
                    '''
                }
                
            }
        }
         
        stage('Terraform Apply') {
            steps {
                dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S') {
                    sh ''' terraform apply -auto-approve \
                       -var="access_key=AKIAVRUVPY6ULII53M3A" \
                       -var="secret_key=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"
                       
                    '''
                }
            }
        }

        stage('Generate Terraform Outputs') {
            steps {
                script {
                    // Export Terraform outputs to JSON
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S'){
                    sh 'terraform output -json > terraform_outputs.json'
                
                    }
                }
            }
        }

        stage('Generate Inventory ') {
            steps {
                script {
                    // Generate Ansible Inventory from Terraform output
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S'){
                    sh 'python3 terraform_inventory.py'
                    }
                }
            }
        }
        stage('Get Admin Conf  ') {
            steps {
                script {
                    // Generate Ansible Inventory from Terraform output
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S'){
                    sh 'export ANSIBLE_HOST_KEY_CHECKING=False && ansible-playbook -i inventory.ini admin.yml'
                    }
                }
            }
        }
        stage('Join Worker Nodes to Kubernetes') {
            steps {
                script {
                    // Run Ansible to join worker nodes to Kubernetes
                    sleep(time: 300, unit: 'SECONDS')
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S'){
                    sh 'export ANSIBLE_HOST_KEY_CHECKING=False && ansible-playbook -i inventory.ini join_worker.yml'
                    }
                }
            }
        }

        
    }

}

Second Jenkins Deployment 
pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPO = '381491857320.dkr.ecr.us-east-1.amazonaws.com/k8s-app-repo'
        KUBE_CONFIG = 'E:/Downloads/aws-challenge/Terraform-Bulding-K8S/admin.conf'  // Update with your path to kubeconfig
    }

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image from your local Dockerfile
                    sh 'docker build -t k8s-app:latest /mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S/docker'
                }
            }
        }

        stage('Push to ECR') {
            steps {
                script {
                    // Login to ECR and push the Docker image
                    sh '''
                    # Get login password for ECR and login
                    aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin 381491857320.dkr.ecr.us-east-1.amazonaws.com
                    
                    # Tag the built image for ECR
                    docker tag k8s-app:latest $ECR_REPO:latest
                    
                    # Push the image to ECR
                    docker push $ECR_REPO:latest
                    '''
                }
            }
        }

        

        stage('Create MySQL Secret to Kubernetes') {
            steps {
                script {
                    // Apply the Kubernetes deployment using the kubeconfig
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S') {
                    sh '''
                    kubectl --kubeconfig=admin.conf apply -f mysql-deployment.yaml
                    '''
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Apply the Kubernetes deployment using the kubeconfig
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S') {
                    sh '''
                    kubectl --kubeconfig=admin.conf apply -f deploy.yaml
                    '''
                    }
                }
            }
        }

        stage('Expose Ports to Kubernetes') {
            steps {
                script {
                    // Apply the Kubernetes deployment using the kubeconfig
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S') {
                    sh '''
                    kubectl --kubeconfig=admin.conf apply -f service.yaml
                    '''
                    }
                }
            }
        }
    }
}
## Contact

If you have any questions or suggestions, please feel free to reach out via [LinkedIn](https://www.linkedin.com/in/nurhaksozer)or email at nicksozer@gmail.com.
