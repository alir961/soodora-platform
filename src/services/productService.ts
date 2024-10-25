import axios from 'axios';
import { getToken } from '@/utils/localStorage';
import {Ref} from "vue";

const apiUrl = import.meta.env.VITE_API_URL;

const fetchProducts = async () => {
    try {
        const response = await axios.get(`${apiUrl}/products`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

const createProduct = async (newProduct: Ref) => {
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

export { fetchProducts, createProduct };
