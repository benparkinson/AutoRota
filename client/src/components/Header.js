import React from 'react';
import { Link } from 'react-router-dom';

const Header = () => {
    return (
        <div className="ui secondary menu">
            <Link to="/" className="ui item">
                <div >
                    <img className="ui middle aligned avatar image" alt="AutoRota icon" src="/robot.png" height="14" width="14" />
                    AutoRota
                </div>
            </Link>
            <Link to="/rotas/new" className="ui item">
                <div>
                    Create
                </div>
            </Link>
            <Link to="/rotas/" className="ui item">
                <div >
                    Rotas
                </div>
            </Link>
            <div className="right menu">
                <Link to="/" className="ui item">
                    <span>Home</span>
                </Link>
            </div>
        </div>
    );
}

export default Header;