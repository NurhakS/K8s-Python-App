pipeline {
    agent any

    environment {
        // Set environment variables if needed
        KUBE_API_SERVER = 'https://10.0.1.138:6443'  // Kubernetes API server URL
        KUBE_TOKEN = 'eyJhbGciOiJSUzI1NiIsImtpZCI6IjJCdGRjMVVRUGlwcUZ2UjNjZFRNTmpUN3J3cEM2Q292NVZabFBjSVBSc0kifQ.eyJhdWQiOlsiaHR0cHM6Ly9rdWJlcm5ldGVzLmRlZmF1bHQuc3ZjLmNsdXN0ZXIubG9jYWwiXSwiZXhwIjoxNzMyMTAyMjAzLCJpYXQiOjE3MzIwOTg2MDMsImlzcyI6Imh0dHBzOi8va3ViZXJuZXRlcy5kZWZhdWx0LnN2Yy5jbHVzdGVyLmxvY2FsIiwia3ViZXJuZXRlcy5pbyI6eyJuYW1lc3BhY2UiOiJkZWZhdWx0Iiwic2VydmljZWFjY291bnQiOnsibmFtZSI6ImRlZmF1bHQiLCJ1aWQiOiI1NTNkNjE2Yi0yNDNmLTQxNTMtOGE3OS1mZjhjMjQ0YmM0NzAifX0sIm5iZiI6MTczMjA5ODYwMywic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmRlZmF1bHQ6ZGVmYXVsdCJ9.JHowL84PwqHHE8LTu8RtJkyMuP3YOUnZRwQGfT-H8Hs5yep3g1cON4K4xdbAe8T9pf_UZmjfJxULeeKXMS-8WIMlQ8qT7pWW16X7W9T9-yYEQrqDNQ0-HteQHa8-0w4W9Hxa3NNWHWTP0Byv-DFzi8tUrb6V-PqvHo5ZlcJlcWjMsmQcx8mrU54SgDtnhCW33kuhISB9uJewZ7XUudnqC7uojuTOxq1W91nr7VSrzSiU1e7teVD0pK2aEqJ_t9ZjNcQa_Xbdm0kFSwbob9HUTS1S8-YDp61ZFfH3oupjt0RFwjo0iMS5YbrX8loNrwo8lZNvghYxEbvllWv4B6oRzg'
        TF_VAR_aws_access_key = 'nope' // AWS access key
        TF_VAR_aws_secret_key = 'nope'      // AWS secret key
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
                       -var="access_key=nope" \
                       -var="secret_key=nope"
                    '''
                }
                
            }
        }
         
        stage('Terraform Apply') {
            steps {
                dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S') {
                    sh ''' terraform apply -auto-approve \
                       -var="access_key=nope" \
                       -var="secret_key=nope"
                       
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
        
        stage('Join Worker Nodes to Kubernetes') {
            steps {
                script {
                    // Run Ansible to join worker nodes to Kubernetes
                    sleep(time: 300, unit: 'SECONDS')
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S'){
                    sh '''
                    
                    export ANSIBLE_HOST_KEY_CHECKING=False && ansible-playbook -i inventory.ini join_worker.yml
                    '''
                    }
                }
            }
        }

        stage('Deploy Application with kubectl') {
            steps {
                script {
                    // Ensure kubectl is configured and connected to your cluster
                    // Deploy your application using kubectl (adjust deployment file accordingly)
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S'){
                    sh '''
                        export KUBERNETES_TOKEN=$KUBE_TOKEN
                        kubectl --token=$KUBERNETES_TOKEN --server=$KUBE_API_SERVER apply -f deploy.yaml   
                        kubectl --token=$KUBERNETES_TOKEN --server=$KUBE_API_SERVER apply -f service.yaml
                    
                    '''  // This could be a deployment, service, etc.
                    }
                }
            }
        }
        stage('Deploy Verify') {
            steps {
                script {
                    // Ensure kubectl is configured and connected to your cluster
                    // Deploy your application using kubectl (adjust deployment file accordingly)
                    dir('/mnt/e/Downloads/aws-challenge/Terraform-Bulding-K8S'){
                    sh '''export KUBERNETES_TOKEN=$KUBE_TOKEN
                            kubectl --token=$KUBERNETES_TOKEN --server=$KUBE_API_SERVER get pods
                        
                    
                    
                    '''  // This could be a deployment, service, etc.
                    }
                }
            }
        }
    }

}
