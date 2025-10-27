import React, { useState } from 'react';
import { bankApi } from './api/bankApi';
import AccountCard from './components/AccountCard';
import TransferForm from './components/TransferForm';
import TransferHistory from './components/TransferHistory';
import './App.css';

function App() {
  const [accounts, setAccounts] = useState([]);
  const [newAccountData, setNewAccountData] = useState({
    name: '',
    email: '',
    age: '',
    city: '',
    initialDeposit: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedTransfers, setSelectedTransfers] = useState(null);

  React.useEffect(() => {
    const loadAccounts = async () => {
      try {
        const allAccounts = await bankApi.getAllAccounts();
        setAccounts(allAccounts);
      } catch (err) {
        console.error('Failed to load accounts:', err);
      }
    };

    loadAccounts();
    
    // This approach can be improved as it is inefficient
    const pollInterval = setInterval(() => {
      loadAccounts();
    }, 3000);

    return () => clearInterval(pollInterval);
  }, []);

  const showMessage = (type, message) => {
    if (type === 'error') {
      setError(message);
      setTimeout(() => setError(''), 5000);
    } else {
      setSuccess(message);
      setTimeout(() => setSuccess(''), 3000);
    }
  };

  const handleCreateAccount = async (e) => {
    e.preventDefault();
    
    if (!newAccountData.name || !newAccountData.email || !newAccountData.age || 
        !newAccountData.city || !newAccountData.initialDeposit) {
      showMessage('error', 'Please fill all fields');
      return;
    }

    if (parseFloat(newAccountData.age) < 18) {
      showMessage('error', 'Must be at least 18 years old');
      return;
    }

    setLoading(true);
    try {
      const response = await bankApi.createAccount(
        newAccountData.name,
        newAccountData.email,
        parseInt(newAccountData.age),
        newAccountData.city,
        newAccountData.initialDeposit
      );
      setAccounts([...accounts, response]);
      setNewAccountData({
        name: '',
        email: '',
        age: '',
        city: '',
        initialDeposit: ''
      });
      showMessage('success', 'Account created successfully!');
    } catch (err) {
      showMessage('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDeposit = async (accountId, amount) => {
    setLoading(true);
    try {
      const response = await bankApi.deposit(accountId, amount);
      setAccounts(accounts.map(acc =>
        acc.accountId === accountId ? { ...acc, balance: response.balance } : acc
      ));
      showMessage('success', 'Deposit successful!');
    } catch (err) {
      showMessage('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleWithdraw = async (accountId, amount) => {
    setLoading(true);
    try {
      const response = await bankApi.withdraw(accountId, amount);
      setAccounts(accounts.map(acc =>
        acc.accountId === accountId ? { ...acc, balance: response.balance } : acc
      ));
      showMessage('success', 'Withdrawal successful!');
    } catch (err) {
      showMessage('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleTransfer = async (fromAccountId, toAccountId, amount) => {
    setLoading(true);
    try {
      const response = await bankApi.transfer(fromAccountId, toAccountId, amount);

      setAccounts(accounts.map(acc => {
        if (acc.accountId === fromAccountId) {
          return { ...acc, balance: response.resultingBalance };
        }
        if (acc.accountId === toAccountId) {
          return { ...acc, balance: response.recipientBalance };
        }
        return acc;
      }));
      
      showMessage('success', 'Transfer successful!');
    } catch (err) {
      showMessage('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleViewTransfers = async (accountId) => {
    setLoading(true);
    try {
      const response = await bankApi.getOutgoingTransfers(accountId);
      setSelectedTransfers(response.transfers);
    } catch (err) {
      showMessage('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1>Fast & Reckless Bank</h1>
      </header>

      <main className="app-main">
        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="create-account-section">
          <h2>Create New Account</h2>
          <form onSubmit={handleCreateAccount} className="create-account-form">
            <div className="form-row">
              <input
                type="text"
                placeholder="Full Name"
                value={newAccountData.name}
                onChange={(e) => setNewAccountData({...newAccountData, name: e.target.value})}
                className="form-input"
                required
              />
              <input
                type="email"
                placeholder="Email"
                value={newAccountData.email}
                onChange={(e) => setNewAccountData({...newAccountData, email: e.target.value})}
                className="form-input"
                required
              />
            </div>
            <div className="form-row">
              <input
                type="number"
                placeholder="Age"
                value={newAccountData.age}
                onChange={(e) => setNewAccountData({...newAccountData, age: e.target.value})}
                className="form-input"
                min="18"
                max="150"
                required
              />
              <input
                type="text"
                placeholder="City"
                value={newAccountData.city}
                onChange={(e) => setNewAccountData({...newAccountData, city: e.target.value})}
                className="form-input"
                required
              />
            </div>
            <div className="form-row">
              <input
                type="number"
                step="0.01"
                placeholder="Initial Deposit (€)"
                value={newAccountData.initialDeposit}
                onChange={(e) => setNewAccountData({...newAccountData, initialDeposit: e.target.value})}
                className="form-input"
                required
              />
              <button
                type="submit"
                disabled={loading}
                className="btn btn-create"
              >
                {loading ? 'Creating...' : 'Create Account'}
              </button>
            </div>
          </form>
        </div>

        {accounts.length > 0 && (
          <>
            <div className="accounts-section">
              <h2>Your Accounts</h2>
              <div className="accounts-grid">
                {accounts.map((account) => (
                  <AccountCard
                    key={account.accountId}
                    account={account}
                    onDeposit={handleDeposit}
                    onWithdraw={handleWithdraw}
                    onViewTransfers={handleViewTransfers}
                  />
                ))}
              </div>
            </div>

            {accounts.length >= 2 ? (
              <TransferForm accounts={accounts} onTransfer={handleTransfer} />
            ) : (
              <div className="transfer-hint">
                <p>Create at least 2 accounts to enable money transfers</p>
              </div>
            )}
          </>
        )}

        {accounts.length === 0 && (
          <div className="empty-state">
            <p>No accounts yet. Create your first account to get started!</p>
          </div>
        )}
      </main>

      {selectedTransfers && (
        <TransferHistory
          transfers={selectedTransfers}
          accounts={accounts}
          onClose={() => setSelectedTransfers(null)}
        />
      )}
    </div>
  );
}

export default App;
