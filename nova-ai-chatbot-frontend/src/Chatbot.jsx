import React, { useState, useEffect, useRef } from 'react';
import axiosClient from './api/axiosClient';
import { useAuth } from './hooks/useAuth';
import ChatHeader from './ChatHeader';
import MessageBubble from './MessageBubble';
import ChatInput from './ChatInput';
import LoginPage from './auth/LoginPage';
import './Chatbot.css';

const Chatbot = () => {
    const { isAuthenticated, loading: authLoading, user } = useAuth();
    
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isListening, setIsListening] = useState(false);
    const [isSpeakingEnabled, setIsSpeakingEnabled] = useState(true);

    const chatRef = useRef(null);
    const recognitionRef = useRef(null);

    // Auto Scroll
    useEffect(() => {
        chatRef.current?.scrollTo({ top: chatRef.current.scrollHeight, behavior: 'smooth' });
    }, [messages]);

    // Voice Recognition Setup
    useEffect(() => {
        if ('SpeechRecognition' in window || 'webkitSpeechRecognition' in window) {
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            recognitionRef.current = new SpeechRecognition();
            recognitionRef.current.continuous = false;
            recognitionRef.current.interimResults = false;
            recognitionRef.current.lang = 'en-IN';

            recognitionRef.current.onresult = (event) => {
                const transcript = event.results[0][0].transcript;
                sendMessage(transcript);
                setIsListening(false);
            };

            recognitionRef.current.onerror = () => setIsListening(false);
        }
    }, []);

    const toggleVoiceInput = () => {
        if (!recognitionRef.current) return alert("Voice input not supported");
        
        if (isListening) {
            recognitionRef.current.stop();
            setIsListening(false);
        } else {
            recognitionRef.current.start();
            setIsListening(true);
        }
    };

    const speakText = (text) => {
        if (!isSpeakingEnabled || !('speechSynthesis' in window)) return;
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = 'en-US';
        utterance.rate = 1.0;
        window.speechSynthesis.speak(utterance);
    };

    const sendMessage = async (text, retryIndex = null) => {
        if (!text?.trim() || loading) return;

        const userMessage = { text: text.trim(), sender: 'user', timestamp: new Date() };

        if (retryIndex !== null) {
            setMessages(prev => prev.map((m, i) => i === retryIndex ? userMessage : m));
        } else {
            setMessages(prev => [...prev, userMessage]);
        }

        setLoading(true);

        try {
            const res = await axiosClient.get(
                `/ai/chat?message=${encodeURIComponent(text)}`
            );

            const aiMessage = {
                text: res.data,
                sender: 'ai',
                timestamp: new Date(),
                isCalendarEvent: res.data.toLowerCase().includes("event successfully")
            };

            setMessages(prev => [...prev, aiMessage]);

            // Auto speak if enabled
            if (isSpeakingEnabled) speakText(res.data);

        } catch (err) {
            const errorMsg = {
                text: "Sorry, I'm unable to respond right now.",
                sender: 'ai',
                isError: true,
                retryMessage: text,
                retryIndex: retryIndex !== null ? retryIndex : messages.length
            };
            setMessages(prev => [...prev, errorMsg]);
        } finally {
            setLoading(false);
        }
    };

    if (authLoading) return <div className="loading-screen">Loading Nova AI...</div>;
    if (!isAuthenticated) return <LoginPage />;

    return (
        <div className="chatbot-container">
            <ChatHeader 
                user={user} 
                isSpeakingEnabled={isSpeakingEnabled}
                onToggleSpeaking={() => setIsSpeakingEnabled(!isSpeakingEnabled)}
            />

            <div className="chat-area" ref={chatRef}>
                {messages.length === 0 && (
                    <div className="empty-state">
                        <h2>Welcome to Nova AI</h2>
                        <p>Speak or type anything. I'm ready to help.</p>
                    </div>
                )}

                {messages.map((msg, index) => (
                    <MessageBubble 
                        key={index} 
                        message={msg} 
                        user={user} 
                        onRetry={() => sendMessage(msg.retryMessage, msg.retryIndex)}
                        onSpeak={speakText}
                    />
                ))}

                {loading && (
                    <div className="message-row ai">
                        <div className="avatar ai-avatar">✦</div>
                        <div className="bubble ai thinking">
                            <span></span><span></span><span></span>
                        </div>
                    </div>
                )}
            </div>

            <ChatInput 
                onSend={sendMessage} 
                loading={loading}
                isListening={isListening}
                onVoiceToggle={toggleVoiceInput}
            />
        </div>
    );
};

export default Chatbot;