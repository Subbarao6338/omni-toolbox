import { CallLog } from '../types';

export interface CallStats {
  totalCalls: number;
  incomingCalls: number;
  outgoingCalls: number;
  missedCalls: number;
  mostFrequentCaller: { name: string; number: string; count: number } | null;
}

export const calculateCallStats = (logs: CallLog[]): CallStats => {
  const stats: CallStats = {
    totalCalls: logs.length,
    incomingCalls: 0,
    outgoingCalls: 0,
    missedCalls: 0,
    mostFrequentCaller: null,
  };

  const callerCounts: Record<string, number> = {};
  const callerDetails: Record<string, string> = {};

  logs.forEach((log) => {
    if (log.type === 'incoming') stats.incomingCalls++;
    else if (log.type === 'outgoing') stats.outgoingCalls++;
    else if (log.type === 'missed') stats.missedCalls++;

    const key = log.number;
    callerCounts[key] = (callerCounts[key] || 0) + 1;
    callerDetails[key] = log.name;
  });

  let maxCount = 0;
  let mostFrequentKey = '';

  Object.entries(callerCounts).forEach(([number, count]) => {
    if (count > maxCount) {
      maxCount = count;
      mostFrequentKey = number;
    }
  });

  if (mostFrequentKey) {
    stats.mostFrequentCaller = {
      name: callerDetails[mostFrequentKey],
      number: mostFrequentKey,
      count: maxCount,
    };
  }

  return stats;
};
