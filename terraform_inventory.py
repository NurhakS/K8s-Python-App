#!/usr/bin/env python3
import json
import subprocess

# Run Terraform to get outputs in JSON format
terraform_output = subprocess.check_output(['terraform', 'output', '-json'])
terraform_output = json.loads(terraform_output)

# Extract values from Terraform output
instance_msr_public_ip = terraform_output["instance_msr_public_ip"]["value"]
worker_nodes_public_ips = terraform_output["instance_wrks_public_ip"]["value"]
ami_user = "ubuntu"  # Change this based on your configuration
private_key_path = "Terraform-key.pem"  # Update this to the actual path of your private key

# Prepare the INI inventory structure
inventory_content = """
[master]
{instance_msr_public_ip} ansible_user={ami_user} ansible_ssh_private_key_file={private_key_path} ansible_python_interpreter=/usr/bin/python3

[worker_nodes]
""".format(
    instance_msr_public_ip=instance_msr_public_ip,
    ami_user=ami_user,
    private_key_path=private_key_path
)

# Add worker node IPs to the inventory
for ip in worker_nodes_public_ips:
    inventory_content += f"{ip} ansible_user={ami_user} ansible_ssh_private_key_file={private_key_path} ansible_python_interpreter=/usr/bin/python3\n"

# Save the inventory to a file
with open("inventory.ini", "w") as inventory_file:
    inventory_file.write(inventory_content)

print("Inventory saved as inventory.ini")
