import React, { useEffect, useState } from 'react';
import { listJobs, type AsyncJob } from '../../api/job';
import styles from './index.module.css';

type JobStats = {
    total: number;
    running: number;
    success: number;
    fail: number;
};

export const Dashboard: React.FC = () => {
    const [jobs, setJobs] = useState<AsyncJob[]>([]);
    const [loading, setLoading] = useState(false);
    const [stats, setStats] = useState<JobStats>({
        total: 0,
        running: 0,
        success: 0,
        fail: 0,
    });
    const [lastUpdated, setLastUpdated] = useState<Date | null>(null);

    const fetchJobs = async () => {
        setLoading(true);
        try {
            const res = await listJobs({ pageSize: 10, pageNumber: 1 });
            const list = res.jobs || [];
            // Prefer backend aggregate stats when available, otherwise fall back to current page size.
            const totalFromCounts =
                (res.totalRunningCount || 0) + (res.totalSuccessCount || 0) + (res.totalFailCount || 0);
            const total = totalFromCounts > 0 ? totalFromCounts : list.length;

            setJobs(list);
            setStats({
                total,
                running: res.totalRunningCount || 0,
                success: res.totalSuccessCount || 0,
                fail: res.totalFailCount || 0,
            });
            setLastUpdated(new Date());
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchJobs();
    }, []);

    const formatTime = (value?: string) => {
        if (!value) return '-';
        const parsed = new Date(value);
        if (Number.isNaN(parsed.getTime())) return value;
        return parsed.toLocaleString();
    };

    const getStatusClass = (status?: string) => {
        const key = status?.toLowerCase() || 'unknown';
        switch (key) {
            case 'running':
                return styles.statusRunning;
            case 'stop':
            case 'stopped':
                return styles.statusStop;
            case 'success':
            case 'succeeded':
                return styles.statusSuccess;
            case 'fail':
            case 'failed':
                return styles.statusFail;
            case 'pending':
                return styles.statusPending;
            default:
                return styles.statusUnknown;
        }
    };

    return (
        <div className={styles.container}>
            <header className={styles.topBar}>
                <div className={styles.titleBlock}>
                    <p className={styles.eyebrow}>CronX Console</p>
                    <h1 className={styles.title}>Job Dashboard</h1>
                    <p className={styles.subtitle}>
                        Track async workloads, spot failures early, and keep schedules predictable.
                    </p>
                </div>
                <div className={styles.actions}>
                    <button
                        onClick={fetchJobs}
                        disabled={loading}
                        className={styles.ghostButton}
                        aria-label="Refresh job list"
                    >
                        {loading ? 'Refreshing...' : 'Refresh'}
                    </button>
                </div>
            </header>

            <section className={styles.statsGrid}>
                <div className={styles.statCard}>
                    <p className={styles.statLabel}>Total Jobs</p>
                    <p className={styles.statValue}>{stats.total}</p>
                    <p className={styles.statHint}>Across active schedules</p>
                </div>
                <div className={styles.statCard}>
                    <p className={styles.statLabel}>Running</p>
                    <p className={styles.statValue}>{stats.running}</p>
                    <p className={styles.statHint}>Currently executing</p>
                </div>
                <div className={styles.statCard}>
                    <p className={styles.statLabel}>Success</p>
                    <p className={styles.statValue}>{stats.success}</p>
                    <p className={styles.statHint}>Last execution OK</p>
                </div>
                <div className={styles.statCard}>
                    <p className={styles.statLabel}>Fail</p>
                    <p className={styles.statValue}>{stats.fail}</p>
                    <p className={styles.statHint}>Needs attention</p>
                </div>
            </section>

            <section className={styles.tableSection}>
                <div className={styles.tableHeader}>
                    <div>
                        <p className={styles.tableTitle}>Jobs</p>
                        <p className={styles.tableMeta}>
                            {lastUpdated ? `Last updated ${lastUpdated.toLocaleTimeString()}` : 'Not updated yet'}
                        </p>
                    </div>
                </div>
                {loading ? (
                    <div className={styles.loadingPanel}>
                        <span className={styles.spinner} />
                        Loading jobs...
                    </div>
                ) : jobs.length === 0 ? (
                    <div className={styles.emptyState}>No jobs found. Try refreshing or check filters.</div>
                ) : (
                    <div className={styles.tableWrap}>
                        <table className={styles.table}>
                            <thead>
                                <tr>
                                    <th>Job Name</th>
                                    <th>Status</th>
                                    <th>Life Cycle</th>
                                    <th>Next Start Time</th>
                                    <th>Description</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {jobs.map((job) => (
                                    <tr key={job.id}>
                                        <td className={styles.primaryCell}>{job.jobName}</td>
                                        <td>
                                            <span className={`${styles.statusBadge} ${getStatusClass(job.jobStatus)}`}>
                                                {job.jobStatus}
                                            </span>
                                        </td>
                                        <td className={styles.mutedCell}>{job.lifeCycleState}</td>
                                        <td className={styles.mutedCell}>{formatTime(job.nextStartTime)}</td>
                                        <td className={styles.descriptionCell} title={job.jobDesc}>
                                            {job.jobDesc || '-'}
                                        </td>
                                        <td>
                                            <button className={styles.actionButton}>Edit</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>
        </div>
    );
};
