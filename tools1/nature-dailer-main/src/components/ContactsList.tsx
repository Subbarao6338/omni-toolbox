import React from 'react';
import { Search, User, Phone } from 'lucide-react';
import { Contact, Theme } from '../types';

interface ContactsListProps {
  theme: Theme;
  contacts: Contact[];
  onCall: (number: string) => void;
}

export default function ContactsList({ theme, contacts, onCall }: ContactsListProps) {
  const isArtistic = theme.id === 'artistic';
  const [searchQuery, setSearchQuery] = React.useState('');

  const filteredContacts = contacts.filter(c => 
    c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    c.number.includes(searchQuery)
  );

  return (
    <div className="flex-1 overflow-y-auto px-6 pt-6">
      <div className="relative mb-6">
        <div className="absolute left-4 top-1/2 -translate-y-1/2 opacity-40">
          <Search size={18} />
        </div>
        <input 
          type="text" 
          placeholder="Search contacts & places"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className={`w-full py-4 pl-12 pr-4 text-sm outline-none transition-all ${
            isArtistic 
              ? 'bg-[#F2F4F0] rounded-3xl text-[#7A8D6E] font-medium placeholder-[#7A8D6E]/50' 
              : 'glass-panel bg-white/5 border-none rounded-2xl focus:ring-2 ring-white/20'
          }`}
        />
      </div>

      <div className="space-y-6">
        {/* Only show favorites if search is empty */}
        {!searchQuery && (
          <div>
            <h3 className={`text-[10px] font-bold tracking-[0.2em] uppercase mb-4 px-2 ${
              isArtistic ? 'font-serif italic text-[#2D4031] opacity-60' : 'opacity-40'
            }`}>Favorites</h3>
            <div className="grid grid-cols-4 gap-4">
              {contacts.slice(0, 4).map((c, i) => (
                <button 
                  key={c.id} 
                  onClick={() => onCall(c.number)}
                  className="flex flex-col items-center gap-2 group"
                >
                  <div className={`w-14 h-14 p-0.5 overflow-hidden group-active:scale-95 transition-transform ${
                    isArtistic ? 'rounded-[18px] bg-[#D9C9B4]' : 'rounded-full border-2 border-white/10'
                  }`}>
                    <img src={c.image} alt={c.name} className={`w-full h-full object-cover ${isArtistic ? 'rounded-[16px]' : 'rounded-full'}`} referrerPolicy="no-referrer" />
                  </div>
                  <span className={`text-[10px] font-medium truncate w-full text-center opacity-80 ${isArtistic ? 'text-[#1A1C19]' : ''}`}>{c.name.split(' ')[0]}</span>
                </button>
              ))}
            </div>
          </div>
        )}

        <div>
          <h3 className={`text-[10px] font-bold tracking-[0.2em] uppercase mb-2 px-2 ${
            isArtistic ? 'font-serif italic text-[#2D4031] opacity-60' : 'opacity-40'
          }`}>{searchQuery ? 'Search Results' : 'All Contacts'}</h3>
          <div className="divide-y divide-white/5">
            {filteredContacts.map((contact, i) => (
              <button
                key={contact.id}
                onClick={() => onCall(contact.number)}
                className="w-full flex items-center gap-4 py-4 px-2 active:bg-white/5 rounded-xl transition-colors group"
              >
                <div className={`w-12 h-12 overflow-hidden flex items-center justify-center ${
                  isArtistic 
                    ? `rounded-[18px] ${['bg-[#7A8D6E]', 'bg-[#C67B58]', 'bg-[#4F6D52]', 'bg-[#D9C9B4]'][i % 4]}` 
                    : 'rounded-full bg-white/10'
                }`}>
                  {contact.image ? (
                    <img src={contact.image} alt={contact.name} className={`w-full h-full object-cover ${isArtistic ? 'rounded-[18px]' : ''}`} referrerPolicy="no-referrer" />
                  ) : (
                    <User size={20} className={isArtistic ? 'text-white' : 'opacity-40'} />
                  )}
                </div>
                <div className="flex-1 text-left">
                  <div className={`font-semibold text-lg ${isArtistic ? 'text-[#1A1C19]' : ''}`} style={{ color: !isArtistic ? theme.colors.text : '' }}>{contact.name}</div>
                  <div className={`text-xs uppercase tracking-tighter ${isArtistic ? 'text-[#7A8D6E]' : 'opacity-40'}`}>{contact.category}</div>
                </div>
                <div className={`transition-opacity ${isArtistic ? 'opacity-40' : 'opacity-0 group-hover:opacity-100'}`}>
                   <div className={`w-8 h-8 flex items-center justify-center rounded-full border ${isArtistic ? 'border-[#F2F4F0]' : 'border-white/10'}`}>
                      <Phone size={14} />
                   </div>
                </div>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
