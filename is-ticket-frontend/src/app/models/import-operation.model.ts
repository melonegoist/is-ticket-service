export type ImportStatus = 'IN_PROGRESS' | 'SUCCESS' | 'FAILED';

export interface ImportOperation {
  id: number;
  status: ImportStatus;
  username: string;
  createdCount?: number;
  startedAt?: string;
  completedAt?: string;
}
