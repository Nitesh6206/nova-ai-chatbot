import React, { useState, useEffect, useRef } from 'react';
import { FaPaperPlane, FaSpinner } from 'react-icons/fa';
import axiosClient from './api/axiosClient';
import { useAuth } from './hooks/useAuth';
import './Chatbot.css';

const Chatbot = () => {
    const { isAuthenticated, loading: authLoading, checkAuth } = useAuth();
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const chatContainerRef = useRef(null);

    // Auto-scroll to bottom
    const scrollToBottom = () => {
        if (chatContainerRef.current) {
            chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
        }
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages, loading]);

    // Handle auth success redirect
    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        if (params.get('auth') === 'success') {
            checkAuth();
            window.history.replaceState({}, '', '/');
        }
    }, [checkAuth]);

    const handleSend = async () => {
        if (input.trim() === '' || loading) return;

        const userMessage = { 
            text: input, 
            sender: 'user',
            timestamp: new Date()
        };

        setMessages(prev => [...prev, userMessage]);
        const messageToSend = input;
        setInput('');
        setLoading(true);
        setError(null);

        try {
            const response = await axiosClient.get(
                `/ai/chat?message=${encodeURIComponent(messageToSend)}`
            );

            const aiMessage = {
                text: response.data,
                sender: 'ai',
                timestamp: new Date()
            };

            setMessages(prev => [...prev, aiMessage]);
        } catch (err) {
            console.error("Error:", err);
            setError("Failed to get response. Please try again.");
            
            const errorMessage = {
                text: "Sorry, I'm having trouble responding right now. Please try again.",
                sender: 'ai',
                timestamp: new Date(),
                isError: true
            };
            setMessages(prev => [...prev, errorMessage]);
        } finally {
            setLoading(false);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    if (authLoading) {
        return (
            <div className="loading-screen">
                <FaSpinner className="spinner" />
                <p>Checking authentication...</p>
            </div>
        );
    }

    if (!isAuthenticated) {
        return (
            <div className="auth-screen">
                <div className="auth-card">
                    <img src="ChatBot.png" alt="Nova AI" className="logo" />
                    <h2>Welcome to Nova AI</h2>
                    <p>Sign in to start chatting with AI</p>
                    <button 
                        className="google-login-btn"
                        onClick={() => window.location.href = 'http://localhost:8080/oauth2/authorization/google'}
                    >
                        Continue with Google
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="chatbot-container">
            {/* Header */}
            <div className="chat-header">
                <div className="header-left">
                    <img src="ChatBot.png" alt="Nova AI" className="chat-logo" />
                    <div>
                        <h1>Nova AI</h1>
                        <p className="status">Always online • Powered by Grok</p>
                    </div>
                </div>
                <div className="header-right">
                    <button className="new-chat-btn">New Chat</button>
                </div>
            </div>

            {/* Chat Area */}
            <div className="chatbox" ref={chatContainerRef}>
                {messages.length === 0 && (
                    <div className="welcome-message">
                        <img src="ai-assistant.png" alt="AI" className="welcome-icon" />
                        <h2>Hello! How can I help you today?</h2>
                        <p>Ask me anything — I'm here to assist.</p>
                    </div>
                )}

                {messages.map((message, index) => (
                    <div 
                        key={index} 
                        className={`message-container ${message.sender}`}
                    >
                        <img
                            src={message.sender === 'user' ? 'user-icon.png' : 'ai-assistant.png'}
                            className="avatar"
                            alt={message.sender}
                        />
                        <div className={`message ${message.sender} ${message.isError ? 'error' : ''}`}>
                            {message.text}
                        </div>
                    </div>
                ))}

                {loading && (
                    <div className="message-container ai">
                        <img src="ai-assistant.png" className="avatar" alt="AI" />
                        <div className="message ai typing">
                            <span></span><span></span><span></span>
                        </div>
                    </div>
                )}
            </div>

            {/* Input Area */}
            <div className="input-area">
                {error && <div className="error-banner">{error}</div>}
                
                <div className="input-container">
                    <input
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={handleKeyPress}
                        placeholder="Type your message here..."
                        disabled={loading}
                    />
                    <button 
                        onClick={handleSend} 
                        disabled={loading || input.trim() === ''}
                        className="send-btn"
                    >
                        {loading ? <FaSpinner className="spinner" /> : <FaPaperPlane />}
                    </button>
                </div>
                <p className="disclaimer">Nova AI can make mistakes. Consider checking important info.</p>
            </div>
        </div>
    );
};

export default Chatbot;