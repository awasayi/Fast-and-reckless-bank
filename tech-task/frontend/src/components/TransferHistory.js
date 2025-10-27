import React from 'react';
import './TransferHistory.css';

function TransferHistory({ transfers, accounts, onClose }) {
  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  const getAccountName = (accountId) => {
    const account = accounts.find(acc => acc.accountId === accountId);
    return account ? account.name : 'Unknown Account';
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>Outgoing Transactions</h3>
          <button onClick={onClose} className="close-btn">×</button>
        </div>
        <div className="modal-body">
          {transfers.length === 0 ? (
            <p className="no-transfers">No transactions yet</p>
          ) : (
            <div className="transfers-list">
              {transfers.map((transfer) => (
                <div key={transfer.transferId} className="transfer-item">
                  <div className="transfer-info">
                    <span className="transfer-to">
                      {transfer.toAccountId ? (
                        <> Transfer to: {getAccountName(transfer.toAccountId)}</>
                      ) : (
                        <> Withdrawal</>
                      )}
                    </span>
                    <span className="transfer-date">{formatDate(transfer.timestampMillis)}</span>
                  </div>
                  <div className="transfer-amounts">
                    <span className="transfer-amount">-€{transfer.amount}</span>
                    <span className="resulting-balance">Balance: €{transfer.resultingBalance}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default TransferHistory;

