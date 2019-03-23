import React from 'react';
import { Link } from 'react-router-dom';

const Header = () => {
    return (
        <div>
            <div className="ui secondary menu">
                <Link to="/" className="ui item">
                    <div >
                        <img className="ui middle aligned avatar image" alt="AutoRota icon" src="/robot.png" height="14" width="14" />
                        <span>AutoRota</span>
                    </div>
                </Link>
                <div className="right menu">
                    <Link to="/" className="ui item">
                        <span>Home</span>
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default Header;