#!/bin/bash
# Initialize Vault with profile service credentials

# Wait for Vault to be ready
echo "Waiting for Vault to start..."
until curl -s http://vault:8200/v1/sys/health >/dev/null; do
  sleep 1
done

# Set Vault token
export VAULT_ADDR=http://vault:8200
export VAULT_TOKEN="${VAULT_DEV_TOKEN:-myroot}"

echo "Enabling KV secrets engine..."
vault secrets enable -version=2 kv

echo "Adding profile service credentials to Vault..."
vault kv put kv/profile-service/dev \
  database.username="${PROFILE_DB_USER:-profile_dev_user}" \
  database.password="${PROFILE_DB_PASSWORD:-profile_dev_password}"

echo "Vault initialization completed!" 