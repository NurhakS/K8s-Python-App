import json

def generate_inventory():
    with open('terraform_outputs.json') as f:
        data = json.load(f)

    # Extract the IP addresses from Terraform outputs
    master_ip = data['instance_msr_public_ip']['value']
    worker_ips = data['instance_wrks_public_ip']['value']

    # Generate Ansible inventory
    inventory = {
        'master': {
            'hosts': [master_ip],
            'vars': {
                'ansible_ssh_user': 'ubuntu',
                'ansible_ssh_private_key_file': 'Terraform-key.pem'
            }
        },
        'workers': {
            'hosts': worker_ips,
            'vars': {
                'ansible_ssh_user': 'ubuntu',
                'ansible_ssh_private_key_file': 'Terraform-key.pem'
            }
        }
    }

    # Save inventory in YAML format
    with open('inventory.yml', 'w') as f:
        json.dump(inventory, f, indent=2)
    bucket_name = data['s3_bucket_name']['value']
    with open('bucket_name.yml', 'w') as f:
     f.write(f"bucket_name: {bucket_name}")
    

if __name__ == "__main__":
    generate_inventory()
