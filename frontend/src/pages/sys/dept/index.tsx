import { useRef, useState } from 'react'
import { Button, Space, Modal, message, Card } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import { sysDeptApi, type SysDeptVO } from '../../../api/sys'
import DeptForm from './DeptForm'

export default function DeptPage() {
  const actionRef = useRef<ActionType>(null)
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<SysDeptVO | undefined>(undefined)
  const [deptTree, setDeptTree] = useState<SysDeptVO[]>([])

  const loadData = async () => {
    try {
      const tree = await sysDeptApi.tree()
      setDeptTree(tree)
    } catch {
      // error handled
    }
  }

  const handleAdd = (parentId?: number) => {
    setFormData({ parentId: parentId ?? 0 })
    setFormOpen(true)
  }

  const handleEdit = (record: SysDeptVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  const handleDelete = async (record: SysDeptVO) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定删除部门"${record.name}"吗？`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await sysDeptApi.delete(record.id!)
          message.success('删除成功')
          loadData()
          actionRef.current?.reload()
        } catch {
          // error handled
        }
      },
    })
  }

  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    loadData()
    actionRef.current?.reload()
  }

  const columns: ProColumns<SysDeptVO>[] = [
    {
      title: '部门名称',
      dataIndex: 'name',
      width: 200,
    },
    {
      title: '部门编码',
      dataIndex: 'code',
      width: 120,
    },
    {
      title: '负责人',
      dataIndex: 'leader',
      width: 100,
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      width: 130,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
    },
    {
      title: '操作',
      width: 200,
      fixed: 'right',
      search: false,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleEdit(record)}>编辑</a>
          <a onClick={() => handleAdd(record.id)}>
            <PlusOutlined style={{ fontSize: 12 }} />
          </a>
          <a onClick={() => handleDelete(record)} style={{ color: '#ff4d4f' }}>
            删除
          </a>
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <ProTable<SysDeptVO>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const tree = await sysDeptApi.tree()
          return {
            data: tree,
            total: tree.length,
            success: true,
          }
        }}
        pagination={false}
        search={false}
        toolbar={{
          actions: [
            <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => handleAdd(0)}>
              新增根部门
            </Button>,
            <Button
              key="export"
              icon={<DownloadOutlined />}
              onClick={() => window.open('/api/sys/dept/export', '_blank')}
            >
              导出Excel
            </Button>,
          ],
        }}
      />
    </Card>
  )
}
