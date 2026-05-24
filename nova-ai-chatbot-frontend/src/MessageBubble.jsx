import React from 'react';
import { FaRedo } from 'react-icons/fa';

const MessageBubble = ({ message, user, onRetry }) => {
    return (
        <div className={`message-row ${message.sender}`}>
            <div className="avatar-container">
                {message.sender === 'user' ? (
                    user?.picture ? (
                        <img src={user.picture} className="avatar" alt="You" />
                    ) : (
                        <div className="avatar-placeholder">You</div>
                    )
                ) : (
                    <div className="ai-avatar">✦</div>
                )}
            </div>

            <div className="message-content">
                <div className={`bubble ${message.sender} ${message.isError ? 'error' : ''}`}>
                    {message.text}
                </div>

                {message.isError && onRetry && (
                    <button className="retry-button" onClick={() => onRetry(message)}>
                        <FaRedo /> Retry
                    </button>
                )}
            </div>
        </div>
    );
};

export default MessageBubble;