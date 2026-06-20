import { GoogleGenAI, Type } from "@google/genai";
import { Message } from "../lib/dataService";

// Initialize Gemini API
const genAI = new GoogleGenAI({ 
  apiKey: process.env.GEMINI_API_KEY as string 
});

export interface SpamDetectionResult {
  id: string;
  isSpam: boolean;
  reason?: string;
}

export const geminiService = {
  /**
   * Scans a list of messages for potential spam using Gemini AI.
   */
  detectSpam: async (messages: Message[]): Promise<SpamDetectionResult[]> => {
    try {
      if (messages.length === 0) return [];

      const prompt = `Analyze the following incoming messages for a messaging app and identify potential spam. 
      Flag messages as spam if they contain suspicious links, unsolicited offers, generic phishing attempts, or repetitive marketing content.
      Provide a brief reason for each flagged message.
      
      Messages to analyze:
      ${messages.map(m => `ID: ${m.id} | Sender: ${m.sender} | Content: ${m.text}`).join('\n')}
      `;

      const response = await genAI.models.generateContent({
        model: "gemini-3-flash-preview",
        contents: prompt,
        config: {
          responseMimeType: "application/json",
          responseSchema: {
            type: Type.ARRAY,
            items: {
              type: Type.OBJECT,
              properties: {
                id: {
                  type: Type.STRING,
                  description: "The ID of the message."
                },
                isSpam: {
                  type: Type.BOOLEAN,
                  description: "Whether the message is potential spam."
                },
                reason: {
                  type: Type.STRING,
                  description: "A brief reason if the message is spam."
                }
              },
              required: ["id", "isSpam"]
            }
          }
        }
      });

      const result = JSON.parse(response.text);
      return result;
    } catch (error) {
      console.error("Spam detection error:", error);
      return [];
    }
  },

  /**
   * Generates a conversational summary of recent messages.
   */
  summarizeMessages: async (messages: Message[]): Promise<string> => {
    try {
      if (messages.length === 0) return "Your forest is quiet and peaceful today.";

      const prompt = `You are a "Forest Spirit" assistant for a nature-themed messaging app. 
      Summarize the following recent messages in a friendly, conversational, and poetic tone (max 2 sentences).
      Focus on key updates or notifications.
      
      Messages:
      ${messages.map(m => `From ${m.sender}: ${m.text}`).join('\n')}`;

      const response = await genAI.models.generateContent({
        model: "gemini-3-flash-preview",
        contents: prompt
      });

      return response.text || "Nature is whispering, but I couldn't catch the words.";
    } catch (error) {
      console.error("Summarization error:", error);
      return "The mystical winds are currently silent.";
    }
  }
};
