import {User} from "@/entity/User";

interface Product {
    slug: string;
    name: string;
    description: string;
    imageUrl: string;
    category: string;
    price: number;
    quantity: number;
    vendor: User;
}

export type {Product};