import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../../api/auth';
import styles from './index.module.css';

export const Login: React.FC = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [errorMSG, setErrorMSG] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setErrorMSG('');
        try {
            await login({ username, password });
            // Token is handled by backend cookie logic (HomeController sets the cookie).
            // If frontend persistence is needed, store it here.
            // Based on HomeController.java:
            // cookie.setHttpOnly(false); -> Frontend can read it if needed, but browser sends it automatically.
            navigate('/dashboard');
        } catch (err: any) {
            setErrorMSG(err.message || 'Login failed');
        } finally {
            setLoading(false);
        }
    };

    const isDisabled = loading || !username || !password;

    return (
        <div className={styles.container}>
            <div className={styles.shell}>
                <section className={styles.brandPanel}>
                    <div className={styles.brandMark}>CronX</div>
                    <h1 className={styles.brandTitle}>Operations Console</h1>
                    <p className={styles.brandSubtitle}>
                        Monitor async jobs, diagnose failures quickly, and keep every schedule on time.
                    </p>
                    <ul className={styles.featureList}>
                        <li>Instant job visibility across environments</li>
                        <li>Clear lifecycle and next-run indicators</li>
                        <li>Reliable audit trail for every change</li>
                    </ul>
                    <div className={styles.tagRow}>
                        <span className={styles.tag}>Secure by design</span>
                        <span className={styles.tag}>Low-latency UI</span>
                    </div>
                </section>

                <section className={styles.card}>
                    <div className={styles.cardHeader}>
                        <p className={styles.cardEyebrow}>CronX Console</p>
                        <h2 className={styles.cardTitle}>Sign in</h2>
                        <p className={styles.cardHint}>Use your CronX credentials to continue.</p>
                    </div>
                    <form onSubmit={handleLogin} className={styles.form} aria-busy={loading}>
                        <div className={styles.formItem}>
                            <label htmlFor="username">Username</label>
                            <input
                                id="username"
                                name="username"
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                disabled={loading}
                                autoComplete="username"
                                placeholder="ops_admin"
                                required
                            />
                        </div>
                        <div className={styles.formItem}>
                            <label htmlFor="password">Password</label>
                            <input
                                id="password"
                                name="password"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                disabled={loading}
                                autoComplete="current-password"
                                placeholder="Your secure password"
                                required
                            />
                        </div>
                        {errorMSG && (
                            <div className={styles.error} role="alert" aria-live="polite">
                                {errorMSG}
                            </div>
                        )}
                        <button type="submit" disabled={isDisabled} className={styles.button}>
                            {loading ? 'Logging in...' : 'Login'}
                        </button>
                        <p className={styles.helper}>Having trouble? Contact your CronX admin.</p>
                    </form>
                </section>
            </div>
        </div>
    );
};
