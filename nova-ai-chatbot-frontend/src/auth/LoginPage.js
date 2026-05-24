import React from 'react';
// import './LoginPage.css';

const LoginPage = () => {
    const handleGoogleLogin = () => {
        window.location.href = 'http://localhost:8080/oauth2/authorization/google';
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="brand">
                    <h1>Nova AI</h1>
                    <p className="tagline">Your Intelligent Workspace</p>
                </div>

                <div className="login-content">
                    <h2>Sign in to continue</h2>
                    <p className="subtitle">Access your personal AI assistant</p>

                    <button className="google-login-button" onClick={handleGoogleLogin}>
                        <img 
                            src="https://www.google.com/images/branding/googleg/1x/googleg_standard_color_128dp.png" 
                            alt="Google" 
                            className="google-icon" 
                        />
                        Continue with Google
                    </button>

                    <p className="terms">
                        By continuing, you agree to our Terms of Service and Privacy Policy
                    </p>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;