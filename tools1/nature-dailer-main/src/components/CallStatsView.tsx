import { CallStats } from '../hooks/useCallStats';
import { Theme } from '../types';

interface StatsViewProps {
  stats: CallStats;
  theme: Theme;
}

export default function CallStatsView({ stats, theme }: StatsViewProps) {
  return (
    <div className="p-6 space-y-6">
      <h2 className="text-2xl font-bold" style={{ color: theme.colors.text }}>Call Statistics</h2>
      
      <div className="grid grid-cols-2 gap-4">
        <StatCard label="Total" value={stats.totalCalls} theme={theme} />
        <StatCard label="Incoming" value={stats.incomingCalls} theme={theme} />
        <StatCard label="Outgoing" value={stats.outgoingCalls} theme={theme} />
        <StatCard label="Missed" value={stats.missedCalls} theme={theme} />
      </div>

      {stats.mostFrequentCaller && (
        <div className="p-4 rounded-2xl glass-panel">
          <h3 className="text-sm opacity-60 mb-1">Most Frequent Caller</h3>
          <p className="font-bold">{stats.mostFrequentCaller.name}</p>
          <p className="text-sm">{stats.mostFrequentCaller.number} ({stats.mostFrequentCaller.count} calls)</p>
        </div>
      )}
    </div>
  );
}

function StatCard({ label, value, theme }: { label: string; value: number; theme: Theme }) {
  return (
    <div className="p-4 rounded-2xl glass-panel flex flex-col items-center justify-center">
      <span className="text-sm opacity-60">{label}</span>
      <span className="text-3xl font-bold" style={{ color: theme.colors.accent }}>{value}</span>
    </div>
  );
}
