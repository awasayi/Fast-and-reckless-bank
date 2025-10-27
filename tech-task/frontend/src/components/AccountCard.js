import React, { useState } from 'react';
import './AccountCard.css';

function AccountCard({ account, onDeposit, onWithdraw, onViewTransfers }) {
  const [amount, setAmount] = useState('');
  const [showDeposit, setShowDeposit] = useState(false);
  const [showWithdraw, setShowWithdraw] = useState(false);

  const handleDeposit = async () => {
    if (amount && parseFloat(amount) > 0) {
      await onDeposit(account.accountId, amount);
      setAmount('');
      setShowDeposit(false);
    }
  };

  const handleWithdraw = async () => {
    if (amount && parseFloat(amount) > 0) {
      await onWithdraw(account.accountId, amount);
      setAmount('');
      setShowWithdraw(false);
    }
  };

  return (
    <div className="account-card">
      <div className="account-header">
        <h3>{account.name || 'Account'}</h3>
        <span className="account-id">{account.accountId}</span>
      </div>
      {account.email && (
        <div className="account-details">
          <div className="detail-item">
            <span className="detail-label">Email:</span>
            <span className="detail-value">{account.email}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">Age:</span>
            <span className="detail-value">{account.age} years</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">City:</span>
            <span className="detail-value">{account.city}</span>
          </div>
        </div>
      )}
      <div className="account-balance">
        <span className="balance-label">Balance</span>
        <span className="balance-amount">€{account.balance}</span>
      </div>
      <div className="account-actions">
        <button onClick={() => setShowDeposit(!showDeposit)} className="btn btn-success">
          Deposit
        </button>
        <button onClick={() => setShowWithdraw(!showWithdraw)} className="btn btn-warning">
          Withdraw
        </button>
        <button onClick={() => onViewTransfers(account.accountId)} className="btn btn-info">
          History
        </button>
      </div>

      {showDeposit && (
        <div className="transaction-form">
          <input
            type="number"
            step="0.01"
            placeholder="Amount"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            className="amount-input"
          />
          <button onClick={handleDeposit} className="btn btn-primary">
            Confirm Deposit
          </button>
        </div>
      )}

      {showWithdraw && (
        <div className="transaction-form">
          <input
            type="number"
            step="0.01"
            placeholder="Amount"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            className="amount-input"
          />
          <button onClick={handleWithdraw} className="btn btn-primary">
            Confirm Withdraw
          </button>
        </div>
      )}
    </div>
  );
}

export default AccountCard;

