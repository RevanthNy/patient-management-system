import { useState } from 'react';
import axios from 'axios';
import {
  Box, Typography, Button, TextField, List, ListItem, ListItemText,
  IconButton, Paper, Divider, Dialog, DialogActions, DialogContent,
  DialogContentText, DialogTitle, Alert, Collapse, Stack
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import PatientForm from './PatientForm';

function App() {
  const [view, setView] = useState('home');
  const [patients, setPatients] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [editingPatient, setEditingPatient] = useState(null);
  const [apiMessage, setApiMessage] = useState({ type: '', text: '' });
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const [patientToDelete, setPatientToDelete] = useState(null);

  const handleSearch = async (termToSearch = searchTerm) => {
    if (!termToSearch.trim()) {
      setPatients([]);
      return;
    }
    setApiMessage({ type: '', text: '' });
    try {
      const response = await axios.get(`http://localhost:8080/api/patients/search?term=${termToSearch}`);
      setPatients(response.data);
      if (response.data.length === 0) {
        setApiMessage({ type: 'info', text: 'No patients found matching your search term.' });
      }
    } catch (error) {
      console.error("Search failed:", error);
      setApiMessage({ type: 'error', text: 'Failed to search for patients.' });
    }
  };

  const openDeleteConfirm = (patient) => {
    setPatientToDelete(patient);
    setDeleteConfirmOpen(true);
  };

  const closeDeleteConfirm = () => {
    setPatientToDelete(null);
    setDeleteConfirmOpen(false);
  };

  const handleDeletePatient = async () => {
    if (!patientToDelete) return;
    try {
      await axios.delete(`http://localhost:8080/api/patients/${patientToDelete.id}`);
      returnToHome({ type: 'success', text: `Patient "${patientToDelete.firstName} ${patientToDelete.lastName}" deleted successfully.` });
      closeDeleteConfirm();
    } catch (error) {
      console.error("Delete failed:", error);
      setApiMessage({ type: 'error', text: 'Failed to delete patient.' });
      closeDeleteConfirm();
    }
  };

  const showEditForm = (patient) => {
    setApiMessage({ type: '', text: '' });
    setEditingPatient(patient);
    setView('edit');
  };

  const returnToHome = (message = null) => {
    setView('home');
    setEditingPatient(null);
    setSearchTerm('');
    setPatients([]);
    if (message) {
      setApiMessage(message);
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', p: 4, bgcolor: '#f4f6f8', minHeight: '100vh' }}>
      <Paper elevation={4} sx={{ width: '100%', maxWidth: '1200px', p: 4, borderRadius: 2 }}>
        <Typography variant="h4" gutterBottom align="center" color="primary" sx={{ fontWeight: 'bold' }}>
          Patient Management Console
        </Typography>

        <Collapse in={!!apiMessage.text}>
          <Alert severity={apiMessage.type || 'info'} sx={{ mb: 2 }} onClose={() => setApiMessage({ type: '', text: '' })}>
            {apiMessage.text}
          </Alert>
        </Collapse>

        {view === 'home' && (
          <Box>
            <Stack spacing={2} sx={{ maxWidth: '600px', margin: 'auto', mb: 4 }}>
              <Button fullWidth variant="contained" size="large" onClick={() => { setView('add'); setApiMessage({ type: '', text: '' }); }}>
                Add New Patient
              </Button>
              <Divider>SEARCH</Divider>
              <TextField fullWidth label="Search by Name, Email, or Phone" variant="outlined" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} onKeyPress={(e) => e.key === 'Enter' && handleSearch()} />
              <Button fullWidth variant="outlined" size="large" onClick={() => handleSearch()}>
                Search Patients
              </Button>
            </Stack>

            {patients.length > 0 && (
              <List>
                {patients.map(p => (
                  <ListItem key={p.id} divider secondaryAction={
                    <Box>
                      <IconButton edge="end" aria-label="edit" onClick={() => showEditForm(p)} sx={{ mr: 1 }}><EditIcon /></IconButton>
                      <IconButton edge="end" aria-label="delete" onClick={() => openDeleteConfirm(p)}><DeleteIcon color="error" /></IconButton>
                    </Box>
                  }>
                    <ListItemText primary={`${p.firstName} ${p.lastName}`} secondary={`${p.email} | ${p.phoneNumber}`} />
                  </ListItem>
                ))}
              </List>
            )}
          </Box>
        )}

        {(view === 'add' || view === 'edit') && (
          <PatientForm patient={editingPatient} onFormSubmit={returnToHome} onCancel={returnToHome} />
        )}
      </Paper>

      <Dialog open={deleteConfirmOpen} onClose={closeDeleteConfirm}>
        <DialogTitle>Confirm Deletion</DialogTitle>
        <DialogContent><DialogContentText>Are you sure you want to delete patient "{patientToDelete?.firstName} {patientToDelete?.lastName}"?</DialogContentText></DialogContent>
        <DialogActions>
          <Button onClick={closeDeleteConfirm}>Cancel</Button>
          <Button onClick={handleDeletePatient} color="error" variant="contained">Delete</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default App;
