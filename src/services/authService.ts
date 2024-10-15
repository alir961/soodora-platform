import axios from 'axios';
import {getToken} from '@/utils/localStorage';

const apiUrl = import.meta.env.VITE_API_URL;

const login = async (email: string, password: string) => {
    try {
        const response = await axios.post(apiUrl + '/auth/login', { email, password });
        return response.data;
    } catch (error) {
        throw error;
    }
};

const signup = async (username: string, firstname: string, lastname: string, email: string, password: string) => {
    try {
        return await axios.post(apiUrl + '/auth/register', {
            username,
            firstname,
            lastname,
            email,
            password
        });
    } catch (error) {
        throw error;
    }
};

const getUserInfo = async () => {
    const token = await getToken();

    if (!token)
        throw new Error('No token found');

    try {
        const response = await axios.get(apiUrl + '/profile', {
            headers: { Authorization: `Bearer ${token}` },
        });
        return response.data;
    } catch (error) {
        throw error;
    }
};

export { login, signup, getUserInfo };
