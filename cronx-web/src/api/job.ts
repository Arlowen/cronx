import request from '../utils/request';

export interface QueryJobFO {
    pageSize: number;
    pageNumber: number;
    jobName?: string;
}

export interface AsyncJob {
    id: number;
    jobName: string;
    jobDesc: string;
    jobStatus: string;
    lifeCycleState: string;
    nextStartTime: string;
}

export interface QueryJobVO {
    jobs: AsyncJob[];
    totalRunningCount: number;
    totalSuccessCount: number;
    totalFailCount: number;
}

export function listJobs(data: QueryJobFO) {
    return request.post<any, QueryJobVO>('/asyncjob/list', data);
}
