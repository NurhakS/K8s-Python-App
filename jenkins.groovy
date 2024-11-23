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
