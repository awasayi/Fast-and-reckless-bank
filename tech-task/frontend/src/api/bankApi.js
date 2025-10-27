const API_BASE_URL = 'http://localhost:8080/api';

const handleResponse = async (response) => {
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.error || 'An error occurred');
  }
  return response.json();
};

export const bankApi = {
  createAccount: async (name, email, age, city, initialDeposit) => {
    const response = await fetch(`${API_BASE_URL}/accounts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, age, city, initialDeposit }),
    });
    return handleResponse(response);
  },

  deposit: async (accountId, amount) => {
    const response = await fetch(`${API_BASE_URL}/accounts/${accountId}/deposit`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ amount }),
    });
    return handleResponse(response);
  },

  withdraw: async (accountId, amount) => {
    const response = await fetch(`${API_BASE_URL}/accounts/${accountId}/withdraw`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ amount }),
    });
    return handleResponse(response);
  },

  transfer: async (fromAccountId, toAccountId, amount) => {
    const response = await fetch(`${API_BASE_URL}/transfers`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fromAccountId, toAccountId, amount }),
    });
    return handleResponse(response);
  },

  getOutgoingTransfers: async (accountId) => {
    const response = await fetch(`${API_BASE_URL}/accounts/${accountId}/outgoing-transfers`);
    return handleResponse(response);
  },

  getAllAccounts: async () => {
    const response = await fetch(`${API_BASE_URL}/accounts`);
    return handleResponse(response);
  },
};

