import React from 'react';
import { FaVolumeUp, FaVolumeMute } from 'react-icons/fa';

const ChatHeader = ({ user, isSpeakingEnabled, onToggleSpeaking }) => {
    return (
        <div className="chat-header">
            <div className="header-left">
                <div className="logo-container">
                    <div className="logo">N</div>
                    <div>
                        <h1>Nova AI</h1>
                        <p className="status">Online • Gemini Powered</p>
                    </div>
                </div>
            </div>

            <div className="header-right">
                {/* AI Speaking Toggle Button - Top Right */}
                <button 
                    className="voice-toggle-btn"
                    onClick={onToggleSpeaking}
                    title={isSpeakingEnabled ? "Mute AI Voice" : "Enable AI Voice"}
                >
                    {isSpeakingEnabled ? <FaVolumeUp size={20} /> : <FaVolumeMute size={20} />}
                    <span className="toggle-label">
                        {isSpeakingEnabled ? "AI Voice On" : "AI Voice Off"}
                    </span>
                </button>

                {user?.picture && (
                    <img 
                        src={user.picture} 
                        alt={user.name || 'User'} 
                        className="user-avatar-header" 
                    />
                )}
                
                <button className="new-chat-button">New Chat</button>
            </div>
        </div>
    );
};

export default ChatHeader;