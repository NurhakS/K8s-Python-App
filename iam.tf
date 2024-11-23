resource "aws_iam_role" "ec2_role" {
  name               = "ec2-k8s-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_policy" "ecr_pull_policy" {
  name        = "ECRPullPolicy"
  description = "Allow EC2 to pull images from ECR"
  policy      = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:GetAuthorizationToken",
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage"
        ]
        Resource = "*"
      }
    ]
  })
}
resource "aws_iam_role_policy_attachment" "attach_ecr_full_access_policy" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess"
}

resource "aws_iam_role_policy_attachment" "attach_ecr_policy" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = aws_iam_policy.ecr_pull_policy.arn
}

resource "aws_iam_instance_profile" "ec2_instance_profile" {
  name = "ec2-k8s-instance-profile"
  role = aws_iam_role.ec2_role.name
}
