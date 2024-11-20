# Use the Alpine-based Python 3 image
FROM python:3.10-alpine

# Install system dependencies, including MySQL client
RUN apk add --no-cache mariadb-dev build-base

# Set the working directory inside the container
WORKDIR /app

# Install Python dependencies
RUN pip install --no-cache-dir flask mysqlclient gunicorn


# Copy the application code into the container
COPY app.py /app/

# Expose the application port
EXPOSE 3000

# Run the app using Gunicorn for better production performance
CMD ["gunicorn", "-b", "0.0.0.0:3000", "app:application"]
