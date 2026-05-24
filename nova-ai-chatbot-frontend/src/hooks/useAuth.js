import { useState, useEffect } from 'react';
import axiosClient from '../api/axiosClient';

export const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);

    useEffect(() => {
        checkAuth();
    }, []);

    const checkAuth = async () => {
    try {
        const res = await axiosClient.get('/ai/health');
        // Agar user info chahiye toh extra endpoint bana sakte ho
        setIsAuthenticated(true);
        setUser(res.data.user || null);   // Agar backend se user info aa raha ho
    } catch (err) {
        setIsAuthenticated(false);
        setUser(null);
    } finally {
        setLoading(false);
    }
};

    return { isAuthenticated, loading, checkAuth, user };
};