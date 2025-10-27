import React, { useState } from 'react';
import './TransferForm.css';

function TransferForm({ accounts, onTransfer }) {
  const [fromAccountId, setFromAccountId] = useState('');
  const [toAccountId, setToAccountId] = useState('');
  const [amount, setAmount] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (fromAccountId && toAccountId && amount && parseFloat(amount) > 0) {
      await onTransfer(fromAccountId, toAccountId, amount);
      setFromAccountId('');
      setToAccountId('');
      setAmount('');
    }
  };

  return (
    <div className="transfer-form-card">
      <h3>Transfer Money</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>From Account</label>
          <select
            value={fromAccountId}
            onChange={(e) => setFromAccountId(e.target.value)}
            required
            className="form-select"
          >
            <option value="">Select account...</option>
            {accounts.map((acc) => (
              <option key={acc.accountId} value={acc.accountId}>
                {acc.name} (€{acc.balance})
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>To Account</label>
          <select
            value={toAccountId}
            onChange={(e) => setToAccountId(e.target.value)}
            required
            className="form-select"
          >
            <option value="">Select account...</option>
            {accounts.map((acc) => (
              <option key={acc.accountId} value={acc.accountId}>
                {acc.name} (€{acc.balance})
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Amount</label>
          <input
            type="number"
            step="0.01"
            placeholder="0.00"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            required
            className="form-input"
          />
        </div>

        <button type="submit" className="btn btn-transfer">
          Transfer Money
        </button>
      </form>
    </div>
  );
}

export default TransferForm;

