import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { reject } from '@/utils';
import { Event } from '@/entity/Event';

async function callRetrieveArtistEvents(artistSlug: string): Promise<Event[]> {
  const response = await axios.get('/events', {
    params: { artistSlug },
    headers: await getAuthorizedHeader(),
  });

  if (response.status === 200 && response.data) return response.data as Event[];
  return reject('Failed to retrieve artist posts.');
}

export { callRetrieveArtistEvents };
