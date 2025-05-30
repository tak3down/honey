'use client';

import React, {useState} from 'react';

interface User {
    username: string;
    sessionId?: string;
}

interface GameQuestion {
    flagUrl: string;
    correctCountry: string;
    options: string[];
    questionNumber: number;
}

interface GameSession {
    sessionId: string;
    score: number;
    totalQuestions: number;
    startTime: number;
    currentQuestion: GameQuestion;
    finished: boolean;
}

interface LeaderboardEntry {
    username: string;
    score: number;
    timeElapsed: number;
    completedAt: string;
}

const API_BASE = 'http://localhost:8080/api';

export default function FlagGame() {
    const [currentPage, setCurrentPage] = useState<'menu' | 'auth' | 'game' | 'results' | 'ranking'>('menu');
    const [user, setUser] = useState<User | null>(null);
    const [gameSession, setGameSession] = useState<GameSession | null>(null);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isLogin, setIsLogin] = useState(true);
    const [selectedAnswer, setSelectedAnswer] = useState<string | null>(null);
    const [showResult, setShowResult] = useState(false);
    const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
    const [loading, setLoading] = useState(false);
    const [userRank, setUserRank] = useState<number | null>(null);

    const authenticateUser = async () => {
        setLoading(true);
        try {
            const endpoint = isLogin ? '/auth/login' : '/auth/register';
            const response = await fetch(`${API_BASE}${endpoint}`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username, password}),
            });

            if (response.ok) {
                const userData = await response.json();
                setUser(userData);
                setCurrentPage('menu');
            } else {
                alert(isLogin ? 'Nieprawidłowe dane logowania' : 'Nazwa użytkownika już istnieje');
            }
        } catch (error) {
            alert('Autoryzacja nie powiodła się');
        }
        setLoading(false);
    };

    const startGame = async () => {
        if (!user) {
            setCurrentPage('auth');
            return;
        }

        setLoading(true);
        try {
            const response = await fetch(`${API_BASE}/game/start`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username: user.username}),
            });

            if (response.ok) {
                const session = await response.json();
                setGameSession(session);
                setCurrentPage('game');
            }
        } catch (error) {
            alert('Nie udało się uruchomić gry');
        }
        setLoading(false);
    };

    const submitAnswer = async (answer: string) => {
        if (!gameSession || selectedAnswer) return;

        setSelectedAnswer(answer);
        setShowResult(true);

        try {
            const response = await fetch(`${API_BASE}/game/answer`, {
                method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({
                    sessionId: gameSession.sessionId, answer: answer,
                }),
            });

            if (response.ok) {
                const updatedSession: GameSession = await response.json();

                setTimeout(() => {
                    if (updatedSession["finished"]) {
                        console.log('koniec gry ->>>');

                        setGameSession(updatedSession);
                        loadLeaderboard(true); // Load leaderboard to determine user rank
                        setCurrentPage('results');

                    } else {

                        setGameSession(updatedSession);
                        setSelectedAnswer(null);
                        setShowResult(false);
                    }
                }, 1500);
            } else {
                alert('Nie udało się przesłać odpowiedzi ' + response);
            }
        } catch (error) {
            alert('Nie udało się przesłać odpowiedzi');
        }
    };

    const loadLeaderboard = async (silent = false) => {
        try {
            const response = await fetch(`${API_BASE}/ranking`);
            if (response.ok) {
                const data = await response.json();
                setLeaderboard(data);

                // Find user's rank if they just finished a game
                if (user && gameSession && silent) {
                    const rank = data.findIndex((entry: LeaderboardEntry) => entry.username === user.username) + 1;
                    setUserRank(rank > 0 ? rank : null);
                }

                if (!silent) {
                    setCurrentPage('ranking');
                }
            } else {
                alert('Nie udało się załadować tablicy wyników ' + response);
            }
        } catch (error) {
            if (!silent) {
                alert('Nie udało się załadować tablicy wyników');
            }
        }
    };

    const formatTime = (milliseconds: number) => {
        const seconds = Math.floor(milliseconds / 1000);
        const minutes = Math.floor(seconds / 60);
        return `${minutes}:${(seconds % 60).toString().padStart(2, '0')}`;
    };

    const logout = () => {
        setUser(null);
        setGameSession(null);
        setCurrentPage('menu');
        setUsername('');
        setPassword('');
    };

    // Navbar Component
    const Navbar = () => (<nav
        className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 border-b border-slate-700/50 backdrop-blur-sm sticky top-0 z-50 shadow-xl">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
                <div className="flex items-center space-x-4">
                    <div className="bg-gradient-to-r from-yellow-500 to-orange-600 bg-clip-text text-transparent">
                        <h1 className="text-2xl font-bold tracking-wide">Honey (miodek)</h1>
                    </div>
                </div>

                <div className="flex items-center space-x-6 flex space-x-3">
                    <button
                        onClick={() => setCurrentPage('menu')}
                        className="text-slate-300 hover:text-white px-3 py-2 rounded-lg hover:bg-slate-700/50 transition-all duration-200"
                    >
                        Strona główna
                    </button>
                    <button
                        onClick={() => loadLeaderboard()}
                        className="text-slate-300 hover:text-white px-3 py-2 rounded-lg hover:bg-slate-700/50 transition-all duration-200"
                    >
                        Topki
                    </button>

                    {user && (<>
                        <button
                            onClick={logout}
                            className="bg-red-600/20 text-red-400 hover:bg-red-600/30 hover:text-red-300 px-4 py-2 rounded-lg transition-all duration-200 border border-red-600/30"
                        >
                            Wyloguj się
                        </button>
                    </>)}

                    {!user && currentPage !== 'auth' && (<button
                        onClick={() => setCurrentPage('auth')}
                        className="bg-gradient-to-r from-yellow-600 to-orange-600 hover:from-yellow-700 hover:to-orange-700 text-white px-6 py-2 rounded-lg font-medium transition-all duration-200 shadow-lg"
                    >
                        Zaloguj się
                    </button>)}

                </div>
            </div>
        </div>
    </nav>);

    const renderMenu = () => (<div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
        <Navbar/>
        <div className="flex items-center justify-center min-h-[calc(100vh-4rem)] px-4">
            <div
                className="bg-gradient-to-br from-slate-800/50 to-slate-900/50 backdrop-blur-xl rounded-2xl shadow-2xl border border-slate-700/50 p-12 max-w-lg w-full">
                <div className="text-center mb-12">
                    <div className="text-6xl mb-4">🌍</div>
                    <h2 className="text-4xl font-bold bg-gradient-to-r from-yellow-400 to-orange-400 bg-clip-text text-transparent mb-4">
                        Honey
                    </h2>
                    <p className="text-slate-400 text-lg">Sprawdź swoją wiedzę geograficzną z flagami z całego
                        świata</p>
                </div>

                <div className="space-y-4">
                    <button
                        onClick={startGame}
                        disabled={loading}
                        className="w-full bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg transform hover:scale-105 disabled:opacity-50 disabled:transform-none"
                    >
                        {loading ? (<div className="flex items-center justify-center space-x-2">
                            <div
                                className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                            <span>Loading...</span>
                        </div>) : ('🎮 Zacznij Grę')}
                    </button>

                    <button
                        onClick={() => loadLeaderboard()}
                        className="w-full bg-gradient-to-r from-yellow-600 to-amber-600 hover:from-yellow-700 hover:to-amber-700 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg transform hover:scale-105"
                    >
                        🏆 Zobacz Topki
                    </button>
                </div>
            </div>
        </div>
    </div>);

    const renderAuth = () => (<div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
        <Navbar/>
        <div className="flex items-center justify-center min-h-[calc(100vh-4rem)] px-4">
            <div
                className="bg-gradient-to-br from-slate-800/50 to-slate-900/50 backdrop-blur-xl rounded-2xl shadow-2xl border border-slate-700/50 p-10 max-w-md w-full">
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-bold text-white mb-2">
                        {isLogin ? 'Witaj Ponownie' : 'Załóż konto'}
                    </h2>
                    <p className="text-slate-400">
                        {isLogin ? 'Zaloguj się, aby zagrać!' : 'Stwórz konto, aby zagrać!'}
                    </p>
                </div>

                <div className="space-y-6">
                    <div>
                        <input
                            type="text"
                            placeholder="Nazwa użytkownika"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className="w-full px-4 py-3 bg-slate-700/50 border border-slate-600/50 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-transparent transition-all duration-200"
                        />
                    </div>

                    <div>
                        <input
                            type="password"
                            placeholder="Hasło"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full px-4 py-3 bg-slate-700/50 border border-slate-600/50 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-transparent transition-all duration-200"
                        />
                    </div>

                    <button
                        onClick={authenticateUser}
                        disabled={loading || !username || !password}
                        className="w-full bg-gradient-to-r from-yellow-600 to-orange-600 hover:from-yellow-700 hover:to-orange-700 text-white font-bold py-3 px-6 rounded-xl transition-all duration-200 shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {loading ? 'Ładowanie...' : (isLogin ? 'Zaloguj Się' : 'Stwórz konto')}
                    </button>

                    <button
                        onClick={() => setIsLogin(!isLogin)}
                        className="w-full text-slate-400 hover:text-white font-medium transition-colors duration-200"
                    >
                        {isLogin ? "Nie masz jeszcze konta? Załóż je!" : 'Posiadasz już konto? Zaloguj się'}
                    </button>
                </div>
            </div>
        </div>
    </div>);

    const renderGame = () => {
        if (!gameSession) return null;

        const {currentQuestion, score, totalQuestions} = gameSession;
        const isCorrect = selectedAnswer === currentQuestion.correctCountry;

        return (<div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
            <Navbar/>
            <div className="py-8 px-4 min-h-[calc(100vh-4rem)]">
                <div className="max-w-4xl mx-auto">
                    <div
                        className="bg-gradient-to-br from-slate-800/50 to-slate-900/50 backdrop-blur-xl rounded-2xl shadow-2xl border border-slate-700/50 p-8">

                        {/* Game Header */}
                        <div className="flex justify-between items-center mb-8">
                            <div className="flex items-center space-x-4">
                                <div
                                    className="bg-gradient-to-r from-yellow-600 to-orange-600 text-white px-4 py-2 rounded-lg font-bold">
                                    Pytanie {currentQuestion.questionNumber} / 10
                                </div>
                            </div>
                            <div className="text-right">
                                <div className="text-2xl font-bold text-white">Wynik: {score}</div>
                                <div
                                    className="text-slate-400">Trafność: {Math.round((score / currentQuestion.questionNumber) * 100)}%
                                </div>
                            </div>
                        </div>

                        {/* Progress Bar */}
                        <div className="mb-8">
                            <div className="bg-slate-700/50 rounded-full h-3">
                                <div
                                    className="bg-gradient-to-r from-yellow-500 to-orange-500 h-3 rounded-full transition-all duration-500"
                                    style={{width: `${(currentQuestion.questionNumber / 10) * 100}%`}}
                                ></div>
                            </div>
                        </div>

                        {/* Flag Display */}
                        <div className="text-center mb-10">
                            <div
                                className="inline-block bg-gradient-to-br from-slate-700/50 to-slate-800/50 p-4 rounded-2xl border border-slate-600/50 shadow-xl">
                                <img
                                    src={currentQuestion.flagUrl}
                                    alt="Flaga państwa"
                                    className="w-80 h-52 object-cover rounded-xl shadow-lg"
                                />
                            </div>
                            <p className="text-slate-300 mt-4 text-lg">Do jakiego kraju należy ta flaga?</p>
                        </div>

                        {/* Answer Options */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {currentQuestion.options.map((option, index) => {
                                let buttonClass = "w-full py-4 px-6 rounded-xl font-semibold text-lg transition-all duration-200 border-2 transform hover:scale-105 ";

                                if (showResult && selectedAnswer) {
                                    if (option === currentQuestion.correctCountry) {
                                        buttonClass += "bg-gradient-to-r from-emerald-600 to-teal-600 text-white border-emerald-500 shadow-lg";
                                    } else if (option === selectedAnswer && !isCorrect) {
                                        buttonClass += "bg-gradient-to-r from-red-600 to-rose-600 text-white border-red-500 shadow-lg";
                                    } else {
                                        buttonClass += "bg-slate-700/30 text-slate-400 border-slate-600/30";
                                    }
                                } else {
                                    buttonClass += "bg-gradient-to-r from-slate-700/50 to-slate-800/50 hover:from-yellow-600/50 hover:to-orange-600/50 text-white border-slate-600/50 hover:border-yellow-500/50 shadow-lg";
                                }

                                return (<button
                                    key={index}
                                    onClick={() => submitAnswer(option)}
                                    disabled={showResult}
                                    className={buttonClass}
                                >
                                    {option}
                                </button>);
                            })}
                        </div>
                    </div>
                </div>
            </div>
        </div>);
    };

    const renderResults = () => {
        if (!gameSession) {
            return null;
        }

        const timeElapsed = Date.now() - gameSession.startTime;
        const accuracy = Math.round((gameSession.score / 10) * 100);

        return (<div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
            <Navbar/>
            <div className="flex items-center justify-center min-h-[calc(100vh-4rem)] px-4">
                <div
                    className="bg-gradient-to-br from-slate-800/50 to-slate-900/50 backdrop-blur-xl rounded-2xl shadow-2xl border border-slate-700/50 p-12 max-w-lg w-full">

                    {/* Results Header */}
                    <div className="text-center mb-8">
                        <div className="text-6xl mb-4">
                            {accuracy >= 90 ? '🏆' : accuracy >= 70 ? '🥈' : accuracy >= 50 ? '🥉' : '📊'}
                        </div>
                        <h2 className="text-4xl font-bold bg-gradient-to-r from-yellow-400 to-orange-400 bg-clip-text text-transparent mb-2">
                            Koniec Gry!
                        </h2>
                    </div>

                    {/* Score Display */}
                    <div className="text-center mb-8">
                        <div
                            className="bg-gradient-to-r from-slate-700/50 to-slate-800/50 rounded-2xl p-6 border border-slate-600/50 mb-6">
                            <div
                                className="text-6xl font-bold bg-gradient-to-r from-emerald-400 to-teal-400 bg-clip-text text-transparent mb-2">
                                {gameSession.score}/10
                            </div>
                            <div className="text-slate-300 text-xl mb-2">
                                Czas: {formatTime(timeElapsed)}
                            </div>
                            <div className="text-slate-400 text-lg">
                                Trafność: {accuracy}%
                            </div>
                        </div>

                        {/* Rank Display */}
                        {userRank && (<div
                            className="bg-gradient-to-r from-yellow-600/20 to-orange-600/20 border border-yellow-500/30 rounded-xl p-4 mb-6">
                            <div className="text-lg text-yellow-300 font-semibold">
                                🎯 Twoja Pozycja: #{userRank}
                            </div>
                        </div>)}
                    </div>

                    {/* Action Buttons */}
                    <div className="space-y-4">
                        <button
                            onClick={() => loadLeaderboard()}
                            className="w-full bg-gradient-to-r from-yellow-600 to-amber-600 hover:from-yellow-700 hover:to-amber-700 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg transform hover:scale-105"
                        >
                            🏆 Wyświetl Topki
                        </button>

                        <button
                            onClick={startGame}
                            className="w-full bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg transform hover:scale-105"
                        >
                            🎮 Graj Ponownie
                        </button>

                        <button
                            onClick={() => setCurrentPage('menu')}
                            className="w-full bg-gradient-to-r from-slate-600 to-slate-700 hover:from-slate-700 hover:to-slate-800 text-white font-bold py-4 px-8 rounded-xl transition-all duration-200 shadow-lg"
                        >
                            🏠 Strona Główna
                        </button>
                    </div>
                </div>
            </div>
        </div>);
    };

    const renderRanking = () => (
        <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
            <Navbar/>
            <div className="py-8 px-4 min-h-[calc(100vh-4rem)]">
                <div className="max-w-4xl mx-auto">
                    <div
                        className="bg-gradient-to-br from-slate-800/50 to-slate-900/50 backdrop-blur-xl rounded-2xl shadow-2xl border border-slate-700/50 p-8">
                        <div className="text-center mb-8">
                            <h2 className="text-4xl font-bold bg-gradient-to-r from-yellow-400 to-orange-400 bg-clip-text text-transparent mb-2">
                                🏆 Topki
                            </h2>
                            <p className="text-slate-400">Najlepsi gracze</p>
                        </div>

                        <div className="space-y-3">
                            {leaderboard.map((entry, index) => {
                                const isCurrentUser = user && entry.username === user.username;

                                return (<div
                                    key={index}
                                    className={`flex justify-between items-center p-6 rounded-xl transition-all duration-200 border ${index === 0 ? 'bg-gradient-to-r from-yellow-600/20 to-amber-600/20 border-yellow-500/30' : index === 1 ? 'bg-gradient-to-r from-slate-600/20 to-slate-700/20 border-slate-400/30' : index === 2 ? 'bg-gradient-to-r from-orange-600/20 to-red-600/20 border-orange-500/30' : isCurrentUser ? 'bg-gradient-to-r from-yellow-600/20 to-orange-600/20 border-yellow-500/30' : 'bg-slate-700/20 border-slate-600/20'}`}
                                >
                                    <div className="flex items-center space-x-4">
                                        <div
                                            className={`w-12 h-12 rounded-full flex items-center justify-center font-bold text-lg ${index === 0 ? 'bg-gradient-to-r from-yellow-500 to-amber-500 text-white' : index === 1 ? 'bg-gradient-to-r from-slate-500 to-slate-600 text-white' : index === 2 ? 'bg-gradient-to-r from-orange-500 to-red-500 text-white' : 'bg-slate-600 text-slate-200'}`}>
                                            {index < 3 ? ['🥇', '🥈', '🥉'][index] : `#${index + 1}`}
                                        </div>
                                        <div>
                                            <div
                                                className={`font-bold text-lg ${isCurrentUser ? 'text-yellow-300' : 'text-white'}`}>
                                                {entry.username} {isCurrentUser && '(Ty)'}
                                            </div>
                                            <div className="text-slate-400 text-sm">
                                                {new Date(entry.completedAt).toLocaleDateString()}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <div className="text-2xl font-bold text-emerald-400">{entry.score}/10</div>
                                        <div
                                            className="text-sm text-slate-400">{formatTime(entry.timeElapsed)}</div>
                                        <div
                                            className="text-xs text-slate-500">{Math.round((entry.score / 10) * 100)}%
                                            trafności
                                        </div>
                                    </div>
                                </div>);
                            })}
                        </div>

                        {leaderboard.length === 0 && (<div className="text-center py-12">
                            <div className="text-4xl mb-4">🎯</div>
                            <p className="text-slate-400 text-lg">Żadna gra nie została jeszcze ukończona. Bądź
                                pierwszy!</p>
                        </div>)}
                    </div>
                </div>
            </div>
        </div>);

    switch (currentPage) {
        case 'menu':
            return renderMenu();
        case 'auth':
            return renderAuth();
        case 'game':
            return renderGame();
        case 'results':
            return renderResults();
        case 'ranking':
            return renderRanking();
        default:
            return renderMenu();
    }
}
