export async function trackEvent(userId: string, eventName: string, eventData: any = {}) {
  console.log('Analytics Event:', { userId, eventName, eventData, timestamp: new Date().toISOString() });
}
