import React, { useState } from 'react';
import { FaPaperPlane, FaSpinner, FaMicrophone, FaMicrophoneSlash } from 'react-icons/fa';
// import './ChatInput.css';

const ChatInput = ({ onSend, loading, isListening, onVoiceToggle }) => {
    const [input, setInput] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (input.trim() && !loading) {
            onSend(input.trim());
            setInput('');
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSubmit(e);
        }
    };

    return (
        <div className="chat-input-wrapper">
            <form className="chat-input-container" onSubmit={handleSubmit}>
                {/* Voice Button */}
                <button
                    type="button"
                    className={`voice-button ${isListening ? 'listening' : ''}`}
                    onClick={onVoiceToggle}
                    disabled={loading}
                    title={isListening ? "Listening..." : "Voice Input"}
                >
                    {isListening ? <FaMicrophoneSlash /> : <FaMicrophone />}
                </button>

                {/* Input Field */}
                <input
                    type="text"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyDown={handleKeyDown}
                    placeholder="Type a message or press 🎤 to speak..."
                    disabled={loading}
                    className="chat-input"
                />

                {/* Send Button */}
                <button 
                    type="submit" 
                    disabled={loading || !input.trim()}
                    className="send-button"
                >
                    {loading ? <FaSpinner className="spin" /> : <FaPaperPlane />}
                </button>
            </form>

            <p className="input-hint">
                Press <span>Enter</span> to send • Voice input supported
            </p>
        </div>
    );
};

export default ChatInput;