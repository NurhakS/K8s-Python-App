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
