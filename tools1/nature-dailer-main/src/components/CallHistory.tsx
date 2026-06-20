import { CallLog, Theme } from '../types';
import CallHistoryItem from './CallHistoryItem';

interface CallHistoryProps {
  theme: Theme;
  calls: CallLog[];
  onCall: (number: string) => void;
  onBlock: (number: string) => void;
}

export default function CallHistory({ theme, calls, onCall, onBlock }: CallHistoryProps) {
  const isArtistic = theme.id === 'artistic';

  return (
    <div className="flex-1 overflow-y-auto pt-6">
      <div className="px-6 mb-8 flex items-center justify-between">
        <h2 className={`text-3xl font-bold ${isArtistic ? 'font-serif text-[#2D4031]' : 'font-display'}`} style={{ color: !isArtistic ? theme.colors.text : '' }}>
          {isArtistic ? 'Recents' : 'Recents'}
        </h2>
      </div>

      <div className="px-2">
        {calls.map(call => (
          <CallHistoryItem
            key={call.id}
            theme={theme}
            call={call}
            onCall={onCall}
            onBlock={onBlock}
          />
        ))}
      </div>
    </div>
  );
}
