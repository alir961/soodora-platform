import axios from 'axios';
import { getToken } from '@/utils/localStorage';

const apiUrl = import.meta.env.VITE_API_URL;

export const fetchProducts = async () => {
    try {
        const response = await axios.get(`${apiUrl}/products`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const createProduct = async (newProduct) => {
    const token = await getToken();
    try {
        await axios.post(`${apiUrl}/products`, newProduct, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    } catch (error) {
        throw error;
    }
};
