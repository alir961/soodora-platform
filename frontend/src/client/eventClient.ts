import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';
import { Event } from '@/entity/Event';

export interface SaveEventPayload {
  type: string;
  title: string;
  description: string;
  url: string;
  thumbnailUrl: string;
  latitude: number;
  longitude: number;
  dateTime: string;
}

async function callCreateEvent(event: SaveEventPayload): Promise<void> {
  return axios.post('/events', event, { headers: await getAuthorizedHeader() });
}

async function callRetrieveArtistEvents(artistSlug: string): Promise<Event[]> {
  const response = await axios.get('/events', {
    params: { artistSlug },
    headers: await getAuthorizedHeader(),
  });

  if (response.status === 200 && response.data) return response.data as Event[];
  return reject('Failed to retrieve artist posts.');
}

async function callUpdateEvent(eventSlug: string, event: SaveEventPayload): Promise<void> {
  return axios.put(`/events/${eventSlug}`, event, { headers: await getAuthorizedHeader() });
}

async function callDeleteEvent(eventSlug: string): Promise<void> {
  return axios.delete(`/events/${eventSlug}`, { headers: await getAuthorizedHeader() });
}

export { callCreateEvent, callRetrieveArtistEvents, callUpdateEvent, callDeleteEvent };
