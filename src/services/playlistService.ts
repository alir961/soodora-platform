import axios from 'axios';
import { getToken } from '@/utils/localStorage';

const apiUrl = import.meta.env.VITE_API_URL;

const fetchPlaylists = async (userSlug: string) => {
    const token = await getToken();
    try {
        const response = await axios.get(`${apiUrl}/playlists?userSlug=${userSlug}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        return response.data;
    } catch (error)  {
        throw error;
    }
};

const createPlaylist = async (newPlaylist: any) => {
    const token = await getToken();
    try {
        await axios.post(`${apiUrl}/playlists`, newPlaylist, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    } catch (error) {
        throw error;
    }
};

export { fetchPlaylists, createPlaylist };
